package com.iskyun.im.ui.auth;

import android.view.LayoutInflater;

import androidx.lifecycle.ViewModelProvider;

import com.iskyun.im.base.BaseActivity;
import com.iskyun.im.databinding.ActivityMobileAuthBinding;
import com.iskyun.im.ui.auth.viewomodel.AuthViewModel;

public class MobileAuthActivity extends BaseActivity<ActivityMobileAuthBinding, AuthViewModel> {

    @Override
    public AuthViewModel onCreateViewModel(ViewModelProvider provider) {
        return provider.get(AuthViewModel.class);
    }

    @Override
    public ActivityMobileAuthBinding onCreateViewBinding(LayoutInflater inflater) {
        return ActivityMobileAuthBinding.inflate(inflater);
    }

    @Override
    public void initBase() {

    }

    @Override
    public int getTitleText() {
        return 0;
    }
}