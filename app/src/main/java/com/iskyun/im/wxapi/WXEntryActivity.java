package com.iskyun.im.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.gyf.immersionbar.ImmersionBar;
import com.iskyun.im.ImLiveApp;
import com.iskyun.im.R;
import com.iskyun.im.data.UserRepository;
import com.iskyun.im.data.bean.User;
import com.iskyun.im.data.req.WXLoginBody;
import com.iskyun.im.data.resp.AppResponse;
import com.iskyun.im.data.resp.UserModel;
import com.iskyun.im.ui.MainActivity;
import com.iskyun.im.ui.auth.AuthActivity;
import com.iskyun.im.ui.login.RegisterActivity;
import com.iskyun.im.ui.login.SmsLoginActivity;
import com.iskyun.im.utils.ActivityUtils;
import com.iskyun.im.utils.LogUtils;
import com.iskyun.im.utils.ToastUtils;
import com.iskyun.im.utils.manager.SpManager;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WXEntryActivity extends AppCompatActivity implements IWXAPIEventHandler {
    private static final String TAG = "WXEntryActivity";
    private IWXAPI api;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImmersionBar.with(this).fullScreen(true).init();
        api = ImLiveApp.get().getWXApi();
        try {
            Intent intent = getIntent();
            api.handleIntent(intent, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq baseReq) {
        LogUtils.e(baseReq.getType() + "");
    }

    @Override
    public void onResp(BaseResp resp) {
        if (resp.errCode == BaseResp.ErrCode.ERR_OK) {
            onRespOk(resp);
        } else {
            ToastUtils.showToast(resp.errStr);
            finish();
        }
    }

    private void onRespOk(BaseResp resp) {
        if (resp.getType() == ConstantsAPI.COMMAND_SENDAUTH) {
            SendAuth.Resp authResp = (SendAuth.Resp) resp;
            final String code = authResp.code;
            WXLoginBody loginBody = new WXLoginBody(code, SpManager.getInstance().getShareTraceChannel());
            UserRepository.get().loginByWeiXin(loginBody).enqueue(new Callback<AppResponse<UserModel>>() {
                @Override
                public void onResponse(@NonNull Call<AppResponse<UserModel>> call, @NonNull Response<AppResponse<UserModel>> response) {
                    if (response.body() != null) {
                        AppResponse<UserModel> appResponse = response.body();
                        if(appResponse.getCode() == 200){
                            UserModel userModel = appResponse.getData();
                            if (userModel == null || userModel.getUserInfo() == null) {
                                failure(appResponse.getMsg());
                                return;
                            }
                            User user = userModel.getUserInfo();
                            SpManager.getInstance().setToken(userModel.getToken());
                            SpManager.getInstance().setCurrentUserId(user.getId());
                            SpManager.getInstance().setCurrentUser(user);
                            if (TextUtils.isEmpty(user.getMobile())) {
                                Bundle args = new Bundle();
                                args.putInt(SmsLoginActivity.MSG_TYPE, SmsLoginActivity.AUTH);
                                ActivityUtils.launcherForResult(ImLiveApp.get().getTopActivity(),
                                        AuthActivity.REQUEST_VERIFIED, args, SmsLoginActivity.class);
                            } else if (user.getAge() == 0) {
                                ActivityUtils.launcher(ImLiveApp.get().getTopActivity(), RegisterActivity.class);
                                new Handler().postDelayed(() -> {
                                    setResult(Activity.RESULT_OK);
                                    finish();
                                }, 50);
                            } else {
                                ActivityUtils.launcher(ImLiveApp.get().getTopActivity(), MainActivity.class);
                                new Handler().postDelayed(ActivityUtils::finishActivityOfMain, 50);
                            }
                        }else {
                            failure(appResponse.getMsg());
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<AppResponse<UserModel>> call, Throwable t) {
                    failure(getString(R.string.login_fail));
                }
            });
        }
    }

    private void failure(String failureMessage){
        ToastUtils.showToast(failureMessage);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == AuthActivity.REQUEST_VERIFIED && data != null) {
            String s = data.getStringExtra(AuthActivity.AUTH_TYPE);
            LogUtils.e(s);
        }
    }
}


