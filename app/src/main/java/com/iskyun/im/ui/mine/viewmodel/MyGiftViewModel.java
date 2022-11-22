package com.iskyun.im.ui.mine.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.iskyun.im.base.BaseListViewModel;
import com.iskyun.im.data.bean.Gift;
import com.iskyun.im.data.resp.ListRecords;

import java.util.List;

public class MyGiftViewModel extends BaseListViewModel<ListRecords<Gift>> {

    private final MutableLiveData<List<Gift>> liveData;
    public int userId;

    public MyGiftViewModel(){
        super();
        liveData=new MutableLiveData<>();
    }

    @Override
    protected void getData() {
    }

    @Override
    public void onSuccess(ListRecords<Gift> myGiftListRecords) {
        if (myGiftListRecords.getRecords()!=null)
            liveData.postValue(myGiftListRecords.getRecords());
    }


    public LiveData<List<Gift>> getFindMyGifts(int userId) {
        userRepository.getFindMyGifts(page,PAGE_SIZE,userId).map(this).subscribe(this);
        return liveData;
    }

}
