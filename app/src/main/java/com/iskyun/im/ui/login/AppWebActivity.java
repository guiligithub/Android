package com.iskyun.im.ui.login;

import android.content.pm.ActivityInfo;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.webkit.WebSettings;

import androidx.lifecycle.ViewModelProvider;

import com.iskyun.im.base.BaseActivity;
import com.iskyun.im.databinding.ActivityAppWebBinding;
import com.iskyun.im.ui.login.viewmodel.LoginViewModel;
import com.mobile.auth.gatewayauth.Constant;

/**
 * 自定义协议展示页
 */
public class AppWebActivity extends BaseActivity<ActivityAppWebBinding, LoginViewModel> {


    @Override
    public LoginViewModel onCreateViewModel(ViewModelProvider provider) {
        return null;
    }

    @Override
    public ActivityAppWebBinding onCreateViewBinding(LayoutInflater inflater) {
        return ActivityAppWebBinding.inflate(inflater);
    }

    @Override
    public void initBase() {
        //https://www.ihuachao.com/banner/forbid_treaty.html
        String mUrl = getIntent().getStringExtra(Constant.PROTOCOL_WEBVIEW_URL);
        if(!TextUtils.isEmpty(mUrl)){
            if(mUrl.startsWith("www.")){
                mUrl = "https://"+ mUrl;
            }
        }
        String mName = getIntent().getStringExtra(Constant.PROTOCOL_WEBVIEW_NAME);
        setRequestedOrientation(
                getIntent().getIntExtra(Constant.PROTOCOL_WEBVIEW_ORIENTATION,
                        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT));

        if(mTvTitle != null){
            String name = mName.replace("《","").replace("》","");
            mTvTitle.setText(name);
        }
        initWebView();
        mViewBinding.webView.loadUrl(mUrl);
    }

    @Override
    public int getTitleText() {
        return 0;
    }

    private void initWebView() {
        WebSettings wvSettings = mViewBinding.webView.getSettings();
        // 是否阻止网络图像
        wvSettings.setBlockNetworkImage(false);
        // 是否阻止网络请求
        wvSettings.setBlockNetworkLoads(false);
        // 是否加载JS
        wvSettings.setJavaScriptEnabled(true);
        wvSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        //覆盖方式启动缓存
        wvSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        // 使用广泛视窗
        wvSettings.setUseWideViewPort(true);
        wvSettings.setLoadWithOverviewMode(true);
        wvSettings.setDomStorageEnabled(true);
        //是否支持缩放
        wvSettings.setBuiltInZoomControls(false);
        wvSettings.setSupportZoom(false);
        //不显示缩放按钮
        wvSettings.setDisplayZoomControls(false);
        wvSettings.setAllowFileAccess(true);
        wvSettings.setDatabaseEnabled(true);
        //缓存相关
        wvSettings.setAppCacheEnabled(true);
        wvSettings.setDomStorageEnabled(true);
        wvSettings.setDatabaseEnabled(true);
    }
}