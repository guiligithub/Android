package com.iskyun.im.ui.login;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.gyf.immersionbar.ImmersionBar;
import com.hyphenate.easecallkit.utils.EaseCallKitUtils;
import com.iskyun.im.BuildConfig;
import com.iskyun.im.ImLiveApp;
import com.iskyun.im.R;
import com.iskyun.im.base.BaseActivity;
import com.iskyun.im.common.Constant;
import com.iskyun.im.data.bean.User;
import com.iskyun.im.data.req.LoginBody;
import com.iskyun.im.databinding.ActivitySmsLoginBinding;
import com.iskyun.im.helper.LiveDataBus;
import com.iskyun.im.ui.MainActivity;
import com.iskyun.im.ui.login.viewmodel.LoginViewModel;
import com.iskyun.im.utils.ActivityUtils;
import com.iskyun.im.utils.ToastUtils;
import com.iskyun.im.ui.login.viewmodel.AuthCodeViewModel;
import com.tencent.mm.opensdk.modelmsg.SendAuth;

/**
 * 验证码登录*/
public class SmsLoginActivity extends BaseActivity<ActivitySmsLoginBinding, LoginViewModel> {

    public static final String MSG_TYPE = "MSG_TYPE";
    public static final int AUTH = 0x100;//认证
    private LoginBody mLoginBody;
    private AuthCodeViewModel mAuthViewModel;

    private int msgType = 0;
    private boolean isCheckeds=false;


    @Override
    public LoginViewModel onCreateViewModel(ViewModelProvider provider) {
        return provider.get(LoginViewModel.class);
    }

    @Override
    public ActivitySmsLoginBinding onCreateViewBinding(LayoutInflater inflater) {
        return ActivitySmsLoginBinding.inflate(inflater);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent != null){
            getMsgType(intent);
            bindingPhone();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PwdLoginActivity.PWD_REQ && resultCode == RESULT_OK){
            ActivityUtils.finishActivityOfMain();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImmersionBar.with(this)
                .keyboardEnable(false, WindowManager.LayoutParams
                        .SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
                /*.keyboardEnable(false, WindowManager.LayoutParams
                        .SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)*/
                .titleBar(mViewBinding.getRoot().findViewById(R.id.tl_header))
                .init();
    }


    @Override
    public void initBase() {
        getMsgType(getIntent());
        initInfo();
        bindingPhone();
        loginSmCb();
    }

    private void initInfo(){
        mLoginBody = new LoginBody();
        //mLoginBody.setLoginInfo("18529189236");
        mLoginBody.setLoginMethod(LoginBody.MOBILE_AND_CODE);
        mLoginBody.setUserType(LoginBody.USER);
        mViewBinding.setLoginBody(mLoginBody);

        mViewBinding.loginSmUserAgreements.setOnClickListener(this::onClick);
        mViewBinding.loginSmPrivacyPolicy.setOnClickListener(this::onClick);
        mViewBinding.tlBack.setOnClickListener(this::onClick);

        mAuthViewModel = new ViewModelProvider(this).get(AuthCodeViewModel.class);
        mViewBinding.setAuthCodeViewModel(mAuthViewModel);

        mViewModel.observerLogin().observe(this, this::setUserLoginResult);

        mViewBinding.loginSmsPwdLogin.setVisibility(BuildConfig.DEBUG?View.VISIBLE:View.GONE);
        mViewBinding.loginSmsPwdLogin.setOnClickListener(v -> ActivityUtils.launcher(SmsLoginActivity.this, PwdLoginActivity.class));
    }

    private void getMsgType(Intent intent){
        if(intent != null && intent.getExtras() != null){
            msgType = intent.getExtras().getInt(MSG_TYPE,0);
        }
    }

    private void bindingPhone(){
        if(msgType == AUTH){
            //mTvTitle.setText(R.string.mobile_auth);
            mViewBinding.loginBtnSign.setText(R.string.binding);
            mViewBinding.loginTvOtherTips.setVisibility(View.GONE);
            mViewBinding.loginIvWx.setVisibility(View.GONE);

//            mViewModel.observerBindPhone().observe(this, s -> {
//                LogUtils.e("observer");
//                Intent data = new Intent();
//                data.putExtra(AuthActivity.AUTH_TYPE, s);
//                setResult(RESULT_OK);
//                finish();
//            });
            mViewModel.observerLogin().observe(this, this::setUserLoginResult);
        }
    }

    @SuppressLint("NonConstantResourceId")
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_tv_send:
                mAuthViewModel.sendCode(mLoginBody.getLoginInfo());
                break;
            case R.id.login_btn_sign:
                btnSign();
                break;
            case R.id.login_iv_wx:
                loginWx();
                break;
            case R.id.login_sm_privacy_policy:
                startAppWeb(com.iskyun.im.common.Constant.PRIVACY_AGREEMENT, getString(R.string.privacy_agreement));
                break;
            case R.id.login_sm_user_agreements:
                startAppWeb(com.iskyun.im.common.Constant.USER_AGREEMENT, getString(R.string.user_agreement));
                break;
            case R.id.tl_back:
                finish();
                break;
            default:
                break;
        }
    }

    private void setUserLoginResult(User user){
        LiveDataBus.get().with(Constant.SMS_LOGIN_SUCCESS).postValue(Constant.SMS_LOGIN_SUCCESS);
        if (user.getAge() == 0) {
            ActivityUtils.launcher(ImLiveApp.get().getTopActivity(), RegisterActivity.class);
            setResult(Activity.RESULT_OK);
            finish();
        } else {
            ActivityUtils.launcher(ImLiveApp.get().getTopActivity(), MainActivity.class);
            ActivityUtils.finishActivityOfMain();
        }
    }

    private void btnSign(){
        if(isCheckeds) {
             if(msgType == AUTH){
                 mViewModel.bindPhone(mLoginBody);
             }else {
                 mViewModel.login(mLoginBody);
             }
        }else {
            ToastUtils.showToast("请勾选用户协议");
        }
    }

    private void loginWx(){
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        //随机数
        req.state = EaseCallKitUtils.getRandomString(10);
        ImLiveApp.get().getWXApi().sendReq(req);
    }

    private void loginSmCb(){
        mViewBinding.loginSmCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if(isChecked){
                    isCheckeds=true;
                }else{
                    isCheckeds=false;
                }
            }
        });
    }

    private void startAppWeb(String url,String title) {
        Intent intent = new Intent();
        intent.setClass(this, AppWebActivity.class);
        intent.putExtra(com.mobile.auth.gatewayauth.Constant.PROTOCOL_WEBVIEW_URL, url);
        intent.putExtra(com.mobile.auth.gatewayauth.Constant.PROTOCOL_WEBVIEW_NAME, title);
        startActivity(intent);
    }

    @Override
    public int getTitleText() {
        return 0;
    }

    @Override
    protected ToolbarHeader getToolbarHeader() {
        return ToolbarHeader.CustomHeader;
    }

}
