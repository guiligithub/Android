package com.iskyun.im.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.iskyun.im.base.BaseViewModel;
import com.iskyun.im.data.bean.Anchor;

public class AnchorViewModel extends BaseViewModel<Anchor> {

    private final MutableLiveData<Anchor> findAnchorLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> weChatLiveData = new MutableLiveData<>();

    @Override
    public void onSuccess(Anchor anchor) {
        findAnchorLiveData.postValue(anchor);
    }

    public LiveData<Anchor> findAnchorDetailById(int anchorId){
        userRepository.findAnchorDetailById(anchorId).map(this)
                .subscribe(this);
        return findAnchorLiveData;
    }

    public LiveData<Anchor> searchAnchorDetailById(int anchorId){
        userRepository.findAnchorById(anchorId).map(this)
                .subscribe(this::onNext, this::onSearchError,this::onComplete);
        return findAnchorLiveData;
    }

    private void onSearchError(Throwable throwable) {
        findAnchorLiveData.setValue(null);
    }

    public void getWeChatNum(int userId){
        userRepository.getWeChatNum(userId).map(new HttpResultFuncStr())
                .subscribe(this::onWeChatNext,this::onError,this::onComplete);
    }

    public LiveData<String> observerWeChat(){
        return weChatLiveData;
    }


    private void onWeChatNext(String s) {
        weChatLiveData.postValue(s);
    }


}
