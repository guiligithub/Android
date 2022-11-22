package com.iskyun.im.ui.auth.viewomodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.iskyun.im.base.BaseViewModel;
import com.iskyun.im.data.req.CallShowBody;

public class CallShowViewModel extends BaseViewModel<String> {

    private final MutableLiveData<String> callShowLiveData = new MutableLiveData<>();

    @Override
    public void onSuccess(String s) {
        callShowLiveData.postValue(s);
    }

    public void callShow(CallShowBody callShowBody){
        setShowDialog(false);
        userRepository.saveCallShow(callShowBody).map(new HttpResultFuncStr()).subscribe(this);
    }

    public LiveData<String> observerCallShow(){
        return callShowLiveData;
    }
}
