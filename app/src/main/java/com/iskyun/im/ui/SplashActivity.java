package com.iskyun.im.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.gyf.immersionbar.ImmersionBar;
import com.hyphenate.chat.EMClient;
import com.iskyun.im.BuildConfig;
import com.iskyun.im.ImLiveApp;
import com.iskyun.im.R;
import com.iskyun.im.base.BaseActivity;
import com.iskyun.im.data.UserRepository;
import com.iskyun.im.data.bean.User;
import com.iskyun.im.data.net.impl.OnResourceParseCallback;
import com.iskyun.im.data.repos.IMClientRepository;
import com.iskyun.im.data.resp.AppResponse;
import com.iskyun.im.data.resp.UserModel;
import com.iskyun.im.databinding.ActivitySplashBinding;
import com.iskyun.im.helper.LoginUIConfig;
import com.iskyun.im.ui.auth.AuthActivity;
import com.iskyun.im.ui.dialog.AppLoadingDialog;
import com.iskyun.im.ui.login.RegisterActivity;
import com.iskyun.im.ui.login.SmsLoginActivity;
import com.iskyun.im.utils.ActivityUtils;
import com.iskyun.im.utils.LogUtils;
import com.iskyun.im.utils.ToastUtils;
import com.iskyun.im.utils.manager.SpManager;
import com.mobile.auth.gatewayauth.PhoneNumberAuthHelper;
import com.mobile.auth.gatewayauth.ResultCode;
import com.mobile.auth.gatewayauth.TokenResultListener;
import com.mobile.auth.gatewayauth.model.TokenRet;

import cn.net.shoot.sharetracesdk.AppData;
import cn.net.shoot.sharetracesdk.ShareTrace;
import cn.net.shoot.sharetracesdk.ShareTraceInstallListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
/**
 * 引导图
 * */
@SuppressLint("CustomSplashScreen")
public class SplashActivity extends BaseActivity<ActivitySplashBinding, ViewModel> implements Callback<AppResponse<UserModel>> {

    public static final int LOGIN_REQ_CODE = 1002;
    private static final int DURATION = 2000;
    private PhoneNumberAuthHelper mPhoneNumberAuthHelper;
    private TokenResultListener mTokenResultListener;
    private LoginUIConfig mUIConfig;
    private AppLoadingDialog appLoadingDialog;
    private int shareTraceRetryCount = 0;//推广失败重试
    private static final int SHARE_TRACE_MAX_RETRY = 1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
    }

    @Override
    public ViewModel onCreateViewModel(ViewModelProvider provider) {
        return null;
    }

    @Override
    public ActivitySplashBinding onCreateViewBinding(LayoutInflater inflater) {
        return ActivitySplashBinding.inflate(inflater);
    }

    @Override
    public void initBase() {
        ImmersionBar.with(this).fullScreen(true).init();
        User user = SpManager.getInstance().getCurrentUser();
        //LogUtils.e(SpManager.getInstance().getShareTraceChannel());
        if (user != null) {
            if (TextUtils.isEmpty(user.getMobile())) {
                Bundle args = new Bundle();
                args.putInt(SmsLoginActivity.MSG_TYPE, SmsLoginActivity.AUTH);
                ActivityUtils.launcherForResult(this,
                        AuthActivity.REQUEST_VERIFIED, args, SmsLoginActivity.class);
                finish();
            } else if (user.getAge() == 0) {
                ActivityUtils.launcher(this, RegisterActivity.class);
                finish();
            } else {
                loadAnimator(mViewBinding.getRoot());
            }
        } else if (!SpManager.getInstance().isInitShareTrace()) {
            getInstallTrace();
        } else {
            initOneKey();
        }
    }

    @Override
    protected ToolbarHeader getToolbarHeader() {
        return ToolbarHeader.CustomHeader;
    }

    @Override
    public int getTitleText() {
        return 0;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_REQ_CODE) {
            if (resultCode == 1) {
                LogUtils.i("登陆成功：" + data.getStringExtra("result"));
            } else {
                //模拟的是必须登录 否则直接退出app的场景
                finish();
            }
        }
    }

    private void loadAnimator(View view) {
        ObjectAnimator mObjectAnimator = ObjectAnimator.ofFloat(view, "alpha",
                1.0f, 0.9f);

        mObjectAnimator.setDuration(DURATION);
        mObjectAnimator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                loadHxSdk();
            }
        });
        mObjectAnimator.start();
    }

    /**
     * 获取推广渠道
     */
    private void getInstallTrace() {
        ShareTrace.getInstallTrace(new ShareTraceInstallListener() {
            @Override
            public void onInstall(AppData appData) {
                LogUtils.e(appData.toString());
                //ToastUtils.showToast(appData.toString());
                SpManager.getInstance().setInitShareTrace(true);
                if (!TextUtils.isEmpty(appData.getParamsData())) {
                    SpManager.getInstance().setShareTraceChannel(appData.getParamsData());
                }
                initOneKey();
            }

            @Override
            public void onError(int i, String s) {
                LogUtils.e(s);
                if(shareTraceRetryCount < SHARE_TRACE_MAX_RETRY){
                    getInstallTrace();
                    shareTraceRetryCount++;
                } else {
                    initOneKey();
                }
            }
        });
    }

    /**
     * 初始化一键登录
     */
    private void initOneKey() {
        sdkInit(BuildConfig.AUTH_SECRET);
        oneKeyLogin();
    }

    private void sdkInit(String secretInfo) {
        appLoadingDialog = AppLoadingDialog.get(this);
        mTokenResultListener = new TokenResultListener() {
            @Override
            public void onTokenSuccess(String s) {
                hideLoadingDialog();
                TokenRet tokenRet;
                try {
                    tokenRet = TokenRet.fromJson(s);
                    if (ResultCode.CODE_START_AUTHPAGE_SUCCESS.equals(tokenRet.getCode())) {
                        LogUtils.i("唤起授权页成功：" + s);
                        //成功后退出splash
                        finish();
                    }

                    if (ResultCode.CODE_SUCCESS.equals(tokenRet.getCode())) {
                        LogUtils.i("获取token成功：" + s);
                        getResultWithToken(tokenRet.getToken());
                        mPhoneNumberAuthHelper.setAuthListener(null);
                        mPhoneNumberAuthHelper.hideLoginLoading();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onTokenFailed(String s) {
                LogUtils.i("获取token失败：" + s);
                hideLoadingDialog();
                TokenRet tokenRet;
                try {
                    tokenRet = TokenRet.fromJson(s);
                    if (ResultCode.CODE_ERROR_USER_CANCEL.equals(tokenRet.getCode())) {
                        //模拟的是必须登录 否则直接退出app的场景
                        finish();
                    } else {
                        ActivityUtils.launcherForResult(SplashActivity.this,
                                1002, SmsLoginActivity.class);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mPhoneNumberAuthHelper.hideLoginLoading();
                mPhoneNumberAuthHelper.setAuthListener(null);
            }
        };
        mPhoneNumberAuthHelper = PhoneNumberAuthHelper.getInstance(this, mTokenResultListener);
        mPhoneNumberAuthHelper.getReporter().setLoggerEnable(BuildConfig.DEBUG);
        mPhoneNumberAuthHelper.setAuthSDKInfo(secretInfo);
        mUIConfig = LoginUIConfig.init(this, mPhoneNumberAuthHelper);
    }

    /**
     * 进入app就需要登录的场景使用
     */
    private void oneKeyLogin() {
        mPhoneNumberAuthHelper = PhoneNumberAuthHelper.getInstance(getApplicationContext(), mTokenResultListener);
        mPhoneNumberAuthHelper.checkEnvAvailable();
        mUIConfig.configAuthPage();
        getLoginToken();
    }

    /**
     * 拉起授权页
     */
    private void getLoginToken() {
        showLoadingDialog();
        mPhoneNumberAuthHelper.getLoginToken(this, 5000);
    }

    private void showLoadingDialog() {
        if (appLoadingDialog != null) {
            appLoadingDialog.show();
        }
    }

    private void hideLoadingDialog() {
        if (appLoadingDialog != null) {
            appLoadingDialog.dismiss();
        }
    }

    /**
     * token 处理
     * 当您成功获取到getLoginToken成功获取Token后，将Token传递至您的服务端，
     * 服务端携带Token调用阿里云的GetMobile接口，即可进行最终的取号操作
     *
     * @param token 一键获取token
     */
    private void getResultWithToken(final String token) {
        /*
          不用要viewModel  因为 splashActivity已经销毁
         */
        UserRepository.get().oneKeyLogin(token).enqueue(this);
    }

    private void loadHxSdk() {
        new IMClientRepository().loadAllInfoFromHX().observe(this, resource -> {

            parseResource(resource, new OnResourceParseCallback<Boolean>() {
                @Override
                public void onSuccess(@Nullable Boolean data) {
                    LogUtils.i("loadAllInfoFromHX--->success");
                    ActivityUtils.launcher(SplashActivity.this, MainActivity.class);
                    finish();
                }

                @Override
                public void onError(int code, String message) {
                    super.onError(code, message);
                    LogUtils.e("loadAllInfoFromHX--->message");
                    EMClient.getInstance().getCurrentUser();
                }
            });
        });
    }

    @Override
    public void onResponse(@NonNull Call<AppResponse<UserModel>> call, @NonNull Response<AppResponse<UserModel>> response) {
        if (response.body() != null) {
            AppResponse<UserModel> appResponse = response.body();
            UserModel userModel = appResponse.getData();
            if (userModel == null || userModel.getUserInfo() == null) {
                return;
            }
            SpManager.getInstance().setToken(userModel.getToken());
            User user = userModel.getUserInfo();
            SpManager.getInstance().setCurrentUserId(user.getId());
            SpManager.getInstance().setCurrentUser(user);
            if (user.getAge() == 0) {
                ActivityUtils.launcher(ImLiveApp.get().getTopActivity(), RegisterActivity.class);
            } else {
                ActivityUtils.launcher(ImLiveApp.get().getTopActivity(), MainActivity.class);
            }
        }
        mPhoneNumberAuthHelper.quitLoginPage();
    }

    @Override
    public void onFailure(@NonNull Call<AppResponse<UserModel>> call, @NonNull Throwable t) {
        LogUtils.e("" + t.getMessage());
        ToastUtils.showToast(R.string.login_fail);
    }
}