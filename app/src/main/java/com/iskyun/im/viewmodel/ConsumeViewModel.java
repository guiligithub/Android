package com.iskyun.im.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.iskyun.im.base.BaseViewModel;
import com.iskyun.im.data.bean.Consume;
import com.iskyun.im.data.req.ChatRecordBody;
import com.iskyun.im.data.resp.AppResponse;

import retrofit2.Call;


/**
 * 消费
 */
public class ConsumeViewModel extends BaseViewModel<Integer> {

    private final MutableLiveData<Integer> deductLiveData = new MutableLiveData<>();

    @Override
    public void onSuccess(Integer s) {
        //deductLiveData.postValue(s);
    }


    public Call<AppResponse<Consume>> deductBalance(int tag, int time, int userId, int callState, int isFree) {
        setShowDialog(false);
        ChatRecordBody body = new ChatRecordBody();
        body.setTag(tag);
        body.setTime(time);
        body.setUserId(userId);
        body.setCallState(callState);
        body.setFoc(isFree);
        return userRepository.deductBalance(body);
    }

    public LiveData<Integer> observerDeduct() {
        return deductLiveData;
    }
}
