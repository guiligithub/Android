package com.iskyun.im.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.iskyun.im.base.BaseViewModel;
import com.iskyun.im.data.bean.GiftType;
import com.iskyun.im.data.bean.GiveGiftResult;
import com.iskyun.im.data.req.GiveGiftBody;

public class GiftViewModel extends BaseViewModel<GiftType> {

    private final MutableLiveData<GiftType> giftLiveData = new MutableLiveData<>();

    private final MutableLiveData<GiveGiftResult> giveGiftLiveData=new MutableLiveData<>();

    @Override
    public void onSuccess(GiftType giftType) {
        giftLiveData.postValue(giftType);
    }

    public LiveData<GiftType> getGifts() {
        userRepository.getGifts().map(this).subscribe(this);
        return giftLiveData;
    }

    public void giveGift(String goodsId, int receiveUserId) {
        if(receiveUserId == 0)
            return;
        GiveGiftBody body = new GiveGiftBody(goodsId, receiveUserId);
        userRepository.giveGift(body).map(new HttpResultFunc<>())
                .subscribe(this::onGiveGiftNext,this::onError,this::onComplete);
    }

    public LiveData<GiveGiftResult> observerGiveGift(){
        return giveGiftLiveData;
    }


    private void onGiveGiftNext(GiveGiftResult giveGiftResult) {
        giveGiftLiveData.postValue(giveGiftResult);
    }


}
