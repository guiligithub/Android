package com.iskyun.im.ui.login;

import android.view.LayoutInflater;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;

import com.iskyun.im.R;
import com.iskyun.im.base.BaseActivity;
import com.iskyun.im.common.Constant;
import com.iskyun.im.databinding.ActivityLoginBinding;
import com.iskyun.im.helper.LiveDataBus;
import com.iskyun.im.ui.login.viewmodel.LoginViewModel;
import com.iskyun.im.utils.ActivityUtils;

public class LoginActivity extends BaseActivity<ActivityLoginBinding, LoginViewModel> {

    @Override
    public LoginViewModel onCreateViewModel(ViewModelProvider provider) {
        return provider.get(LoginViewModel.class);
    }

    @Override
    public ActivityLoginBinding onCreateViewBinding(LayoutInflater inflater) {
        return ActivityLoginBinding.inflate(inflater);
    }

    @Override
    public void initBase() {
        mViewBinding.loginIvPhone.setOnClickListener(this::onClick);
        //mViewBinding.loginGroupProtocol.setVisibility(View.VISIBLE);

        /**
         * 短信登录成功
         */
        LiveDataBus.get().with(Constant.SMS_LOGIN_SUCCESS,String.class).observe(this, s -> finish());
    }

    @Override
    protected void onClick(View view) {
        super.onClick(view);
        if(view.getId() == R.id.login_iv_phone){
            ActivityUtils.launcher(LoginActivity.this, SmsLoginActivity.class);
//            if(mViewBinding.loginCb.isChecked()){
//            }else {
//                ToastUtils.showToast(R.string.terms_of_service);
//            }
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


}