package com.iskyun.im.ui.frag;

import android.os.Bundle;
import android.view.LayoutInflater;

import com.iskyun.im.base.BaseDialogFragment;
import com.iskyun.im.databinding.FragmentBeautySettingBinding;

import cn.tillusory.sdk.TiSDKManager;

public class BeautySettingFragment extends BaseDialogFragment<FragmentBeautySettingBinding> {

    @Override
    public FragmentBeautySettingBinding onCreateViewBinding(LayoutInflater inflater) {
        return FragmentBeautySettingBinding.inflate(inflater);
    }

    @Override
    public void onStart() {
        super.onStart();
        setDialogParams(0);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        mViewBinding.beautySettingPanel.init(TiSDKManager.getInstance());
        mViewBinding.beautySettingPanel.showBeautyModeContainer();
    }


}
