package com.iskyun.im.ui.login;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;

import com.iskyun.im.ImLiveApp;
import com.iskyun.im.R;
import com.iskyun.im.base.BaseActivity;
import com.iskyun.im.common.Constant;
import com.iskyun.im.data.bean.User;
import com.iskyun.im.data.req.LoginBody;
import com.iskyun.im.databinding.ActivityPwdLoginBinding;
import com.iskyun.im.helper.LiveDataBus;
import com.iskyun.im.ui.MainActivity;
import com.iskyun.im.ui.login.viewmodel.LoginViewModel;
import com.iskyun.im.utils.ActivityUtils;

public class PwdLoginActivity extends BaseActivity<ActivityPwdLoginBinding, LoginViewModel> {
    public static final int PWD_REQ = 0x10;
    private LoginBody mLoginBody;
    @Override
    public LoginViewModel onCreateViewModel(ViewModelProvider provider) {
        return provider.get(LoginViewModel.class);
    }

    @Override
    public ActivityPwdLoginBinding onCreateViewBinding(LayoutInflater inflater) {
        return ActivityPwdLoginBinding.inflate(inflater);
    }

    @Override
    public void initBase() {
        mLoginBody = new LoginBody();
        //mLoginBody.setLoginInfo("18529189236");
        mLoginBody.setLoginMethod(LoginBody.ACCOUNT_AND_PASSWORD);
        mLoginBody.setUserType(LoginBody.USER);
        mViewBinding.setLoginBody(mLoginBody);


        mViewModel.observerLogin().observe(this, this::setUserLoginResult);

        mViewBinding.pwdLoginBtnSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.login(mLoginBody);
            }
        });
    }

    @Override
    public int getTitleText() {
        return R.string.login;
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
}
