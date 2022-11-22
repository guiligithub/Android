package com.iskyun.im.ui.login.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.hyphenate.easeui.domain.EaseUser;
import com.iskyun.im.base.BaseViewModel;
import com.iskyun.im.data.bean.User;
import com.iskyun.im.data.repos.IMClientRepository;
import com.iskyun.im.data.req.BindPhoneBody;
import com.iskyun.im.data.req.LoginBody;
import com.iskyun.im.data.resp.Resource;
import com.iskyun.im.data.resp.UserModel;
import com.iskyun.im.utils.AuthUtils;
import com.iskyun.im.utils.manager.SpManager;

public class LoginViewModel extends BaseViewModel<UserModel> {
    private final IMClientRepository mRepository;
    private final MediatorLiveData<Resource<EaseUser>> hxLoginObservable;
    private final MutableLiveData<User> userInfoLiveData;
    private final MutableLiveData<String> bindPhoneLiveData = new MutableLiveData<>();

    public LoginViewModel() {
        hxLoginObservable = new MediatorLiveData<>();
        mRepository = new IMClientRepository();
        userInfoLiveData = new MutableLiveData<>();
    }

    @Override
    public void onSuccess(UserModel userModel) {
        if (userModel == null || userModel.getUserInfo() == null) {
            return;
        }
        User userInfo = userModel.getUserInfo();
        userInfoLiveData.setValue(userModel.getUserInfo());
        /**
         * 保存用户信息
         */
        SpManager.getInstance().setToken(userModel.getToken());
        SpManager.getInstance().setCurrentUserId(userInfo.getId());
        SpManager.getInstance().setCurrentUser(userInfo);
    }


    public void login(LoginBody loginBody) {
        if(loginBody.getLoginMethod().equals(LoginBody.MOBILE_AND_CODE)){
            if (!AuthUtils.verifyAuthCode(loginBody.getLoginSecret())
                    || !AuthUtils.verifyNumberPhone(loginBody.getLoginInfo())) {
                return;
            }
        }else {
            if (!AuthUtils.verifyPassword(loginBody.getLoginSecret())
                    || !AuthUtils.verifyNumberPhone(loginBody.getLoginInfo())) {
                return;
            }
        }
        userRepository.login(loginBody).map(this).subscribe(this);
    }

    public void bindPhone(LoginBody loginBody){
        if (!AuthUtils.verifyAuthCode(loginBody.getLoginSecret())
                || !AuthUtils.verifyNumberPhone(loginBody.getLoginInfo())) {
            return;
        }
        BindPhoneBody body = new BindPhoneBody();
        body.setCode(loginBody.getLoginSecret());
        body.setPhone(loginBody.getLoginInfo());
        userRepository.bindPhone(body).map(this).subscribe(this);
    }

    private void bindPhoneNext(String s) {
        bindPhoneLiveData.postValue(s);
    }


    /**
     * 登录环信
     */
    public void loginInIm(String userName, String password) {
        hxLoginObservable.addSource(mRepository.loginToServer(userName, password, false),
                hxLoginObservable::setValue);

    }

    public LiveData<String> observerBindPhone(){
        return bindPhoneLiveData;
    }

    public LiveData<User> observerLogin(){
        return userInfoLiveData;
    }


    public LiveData<Resource<EaseUser>> getHxLoginObservable() {
        return hxLoginObservable;
    }


}
