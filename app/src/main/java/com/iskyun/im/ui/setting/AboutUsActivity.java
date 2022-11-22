package com.iskyun.im.ui.setting;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;

import com.iskyun.im.R;
import com.iskyun.im.base.BaseActivity;
import com.iskyun.im.databinding.ActivitySettingAboutUsBinding;
import com.iskyun.im.ui.home.HomeViewModel;
import com.iskyun.im.ui.login.AppWebActivity;
import com.mobile.auth.gatewayauth.Constant;

public class AboutUsActivity extends BaseActivity<ActivitySettingAboutUsBinding, HomeViewModel> {
    @Override
    public HomeViewModel onCreateViewModel(ViewModelProvider provider) {
        return null;
    }

    @Override
    public ActivitySettingAboutUsBinding onCreateViewBinding(LayoutInflater inflater) {
        return ActivitySettingAboutUsBinding.inflate(inflater);
    }

    @Override
    public void initBase() {

    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_agreement:
                startAppWeb(com.iskyun.im.common.Constant.USER_AGREEMENT, getString(R.string.user_agreement));
                break;
            case R.id.privacy_policy:
                startAppWeb(com.iskyun.im.common.Constant.PRIVACY_AGREEMENT, getString(R.string.privacy_agreement));
                break;
            case R.id.platform_code_of_conduct:
//                intent.setClass(this, AppWebActivity.class);
//                intent.putExtra(Constant.PROTOCOL_WEBVIEW_URL, "https://www.baidu.com");
//                intent.putExtra(Constant.PROTOCOL_WEBVIEW_NAME, "百度");
//                startActivity(intent);
                break;

        }

    }

    private void startAppWeb(String url,String title) {
        Intent intent = new Intent();
        intent.setClass(this, AppWebActivity.class);
        intent.putExtra(Constant.PROTOCOL_WEBVIEW_URL, url);
        intent.putExtra(Constant.PROTOCOL_WEBVIEW_NAME, title);
        startActivity(intent);
    }

    @Override
    public int getTitleText() {
        return R.string.about;
    }
}
