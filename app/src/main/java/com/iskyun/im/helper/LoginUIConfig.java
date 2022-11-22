package com.iskyun.im.helper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;

import com.hyphenate.easecallkit.utils.EaseCallKitUtils;
import com.iskyun.im.ImLiveApp;
import com.iskyun.im.R;
import com.iskyun.im.common.Constant;
import com.iskyun.im.ui.login.SmsLoginActivity;
import com.iskyun.im.utils.ActivityUtils;
import com.iskyun.im.utils.DisplayUtils;
import com.iskyun.im.utils.LogUtils;
import com.iskyun.im.utils.ToastUtils;
import com.mobile.auth.gatewayauth.AuthRegisterXmlConfig;
import com.mobile.auth.gatewayauth.AuthUIConfig;
import com.mobile.auth.gatewayauth.PhoneNumberAuthHelper;
import com.mobile.auth.gatewayauth.ResultCode;
import com.mobile.auth.gatewayauth.ui.AbstractPnsViewDelegate;
import com.tencent.mm.opensdk.modelmsg.SendAuth;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginUIConfig {
    public Activity mActivity;
    public Context mContext;
    public PhoneNumberAuthHelper mAuthHelper;

    public static LoginUIConfig init(Activity activity, PhoneNumberAuthHelper authHelper) {
        return new LoginUIConfig(activity, authHelper);
    }

    public LoginUIConfig(Activity activity, PhoneNumberAuthHelper authHelper) {
        mActivity = activity;
        mContext = activity.getApplicationContext();
        mAuthHelper = authHelper;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void configAuthPage() {
        mAuthHelper.removeAuthRegisterXmlConfig();
        mAuthHelper.removeAuthRegisterViewConfig();
        int authPageOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT;
        if (Build.VERSION.SDK_INT == 26) {
            authPageOrientation = ActivityInfo.SCREEN_ORIENTATION_BEHIND;
        }
        mAuthHelper.setUIClickListener((code, context, json) -> {
            JSONObject jsonObj = null;
            try {
                if (!TextUtils.isEmpty(json)) {
                    jsonObj = new JSONObject(json);
                }
            } catch (JSONException e) {
                jsonObj = new JSONObject();
            }
            switch (code) {
                case ResultCode.CODE_ERROR_USER_CANCEL:
                    mAuthHelper.quitLoginPage();
                    mActivity.finish();
                    break;
                case ResultCode.CODE_ERROR_USER_LOGIN_BTN:
                    if (jsonObj != null && !jsonObj.optBoolean("isChecked")) {
                        ToastUtils.showToast(R.string.terms_of_service);
                    }
                    break;
                default:
                    break;
            }
        });
        //sdk默认控件的区域是marginTop50dp
        int designHeight = DisplayUtils.px2dp(DisplayUtils.getHeightPixels()) - 50;
        int unit = designHeight / 10;
        mAuthHelper.addAuthRegisterXmlConfig(new AuthRegisterXmlConfig.Builder()
                .setLayout(R.layout.activity_login, new AbstractPnsViewDelegate() {
                    @Override
                    public void onViewCreated(View view) {
                        findViewById(R.id.login_iv_back).setOnClickListener(new LoginClickListener());
                        findViewById(R.id.login_iv_phone).setOnClickListener(new LoginClickListener());
                        findViewById(R.id.login_tv_phone).setOnClickListener(new LoginClickListener());
                        findViewById(R.id.login_iv_wx).setOnClickListener(new LoginClickListener());
                    }
                })
                .build());
        mAuthHelper.setActivityResultListener((i, i1, intent) -> {
            LogUtils.e("-----LoginUIConfig------");
            if(i1 == Activity.RESULT_OK){
                LogUtils.e("-----LoginUIConfig---1---");
                if(1002 == i){
                    LogUtils.e("-----LoginUIConfig---2---");
                    mAuthHelper.quitLoginPage();
                }
            }
        });
        mAuthHelper.setAuthUIConfig(new AuthUIConfig.Builder()
                .setAppPrivacyOne("《"+mActivity.getString(R.string.privacy_agreement)+"》", Constant.USER_AGREEMENT)
                .setAppPrivacyTwo("《"+mActivity.getString(R.string.user_agreement)+"》", Constant.PRIVACY_AGREEMENT)
                .setAppPrivacyColor(Color.WHITE, Color.parseColor("#157ff5"))
                .setPrivacyState(true)
                .setPrivacyBefore(mActivity.getString(R.string.read_and_agree))
                .setNavHidden(true)
                .setLogoHidden(true)
                .setSloganHidden(true)
                .setSwitchAccHidden(true)
                .setCheckboxHidden(false)
                .setCheckedImgDrawable(mActivity.getDrawable(R.mipmap.icon_white_check))
                .setUncheckedImgDrawable(mActivity.getDrawable(R.mipmap.icon_white_uncheck))
                .setLogBtnToastHidden(true)
                .setLightColor(true)
                .setNumFieldOffsetY((int) (unit * 5.5))
                .setLogBtnOffsetY(unit * 6)
                .setWebViewStatusBarColor(Color.TRANSPARENT)
                .setStatusBarColor(Color.TRANSPARENT)
                .setStatusBarUIFlag(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
                .setWebNavTextSizeDp(20)
                .setProtocolAction("com.iskyun.im.protocolWeb")
                .setNumberSizeDp(20)
                .setNumberColor(Color.BLACK)
                .setAuthPageActIn("in_activity", "out_activity")
                .setAuthPageActOut("in_activity", "out_activity")
                .setVendorPrivacyPrefix("《")
                .setVendorPrivacySuffix("》")
                //.setPageBackgroundDrawable(mActivity.getDrawable(R.mipmap.icon_login_bg_light))
                //.setLogoImgPath("mytel_app_launcher")
                .setLogBtnBackgroundPath("btn_common_bg")
                .setLogBtnHeight(48)
                .setScreenOrientation(authPageOrientation)
                .create());
    }

    private class LoginClickListener implements View.OnClickListener {

        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.login_iv_back:
                    mAuthHelper.quitLoginPage();
                    break;
                case R.id.login_iv_phone:
                case R.id.login_tv_phone:
                    ActivityUtils.launcherForResult(mActivity, 1002, SmsLoginActivity.class);
                    //mAuthHelper.quitLoginPage();
                    break;
                case R.id.login_iv_wx:
                case R.id.login_tv_wx:
                    loginWx();
                    break;
            }
        }

        private void loginWx(){
            SendAuth.Req req = new SendAuth.Req();
            req.scope = "snsapi_userinfo";
            //随机数
            req.state = EaseCallKitUtils.getRandomString(10);
            ImLiveApp.get().getWXApi().sendReq(req);
        }
    }
}
