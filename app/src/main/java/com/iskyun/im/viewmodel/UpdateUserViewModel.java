package com.iskyun.im.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.iskyun.im.base.BaseViewModel;
import com.iskyun.im.data.bean.User;
import com.iskyun.im.data.req.UserBody;
import com.iskyun.im.utils.GsonUtils;
import com.iskyun.im.utils.manager.SpManager;

/**
 * 用户信息修改
 */
public class UpdateUserViewModel extends BaseViewModel<UserBody> {

    private final MutableLiveData<User> updateObserver = new MutableLiveData<>();
    private final MutableLiveData<String> randomNameLiveData = new MutableLiveData<>();

    @Override
    public void onSuccess(UserBody userBody) {
        User user = SpManager.getInstance().getCurrentUser();
        user.setAge(userBody.getAge());
        user.setNickname(userBody.getNickname());
        user.setHeadUrl(userBody.getHeadUrl());
        user.setSex(userBody.getSex());
        user.setProfession(userBody.getProfession());
        user.setCity(userBody.getCity());
        user.setHeight(userBody.getHeight());
        user.setAffectiveState(userBody.getAffectiveState());
        user.setTag(userBody.getTag());
        user.setSignature(userBody.getSignature());
        user.setWxNumber(userBody.getWxNumber());
        SpManager.getInstance().setCurrentUser(user);
        SpManager.getInstance().setCurrentUserId(user.getId());
        updateObserver.postValue(user);
    }

    public void update(User user){
        UserBody userBody = GsonUtils.fromJson(GsonUtils.toJson(user), UserBody.class);
        userRepository.update(userBody).map(this).subscribe(this);
    }

    public LiveData<User> observerUpdate(){
        return updateObserver;
    }

    public void userRandomName(){
        userRepository.userRandomName().map(new HttpResultFuncStr())
                .subscribe(this::onRandomNameNext,this::onError,this::onComplete);
    }

    public LiveData<String> observerRandomName(){
        return this.randomNameLiveData;
    }

    private void onRandomNameNext(String string){
        randomNameLiveData.postValue(string);
    }
}
