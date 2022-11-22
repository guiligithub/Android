package com.iskyun.im.ui.frag;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.webkit.WebSettings;

import com.iskyun.im.base.BaseDialogFragment;
import com.iskyun.im.databinding.FragmentAgreementBinding;

public class AgreementFragment extends BaseDialogFragment<FragmentAgreementBinding> {

    private static final String URL = "URL";
    private static final String TITLE = "TITLE";

    public static AgreementFragment newInstance(String url,String title) {
        AgreementFragment fragment = new AgreementFragment();
        Bundle args = new Bundle();
        args.putString(URL, url);
        args.putString(TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        setDialogPaddingParams();
    }

    @Override
    public FragmentAgreementBinding onCreateViewBinding(LayoutInflater inflater) {
        return FragmentAgreementBinding.inflate(inflater);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        initWebView();
        if (getArguments() != null) {
            String mUrl = getArguments().getString(URL);
            String mTitle = getArguments().getString(TITLE);
            if (!TextUtils.isEmpty(mUrl)) {
                if (mUrl.startsWith("www.")) {
                    mUrl = "https://" + mUrl;
                }
            }
            mViewBinding.webView.loadUrl(mUrl);
            mViewBinding.agreementTvTitle.setText(mTitle);
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
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
