package com.iskyun.im.ui.message.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.iskyun.im.base.BaseListViewModel;
import com.iskyun.im.data.bean.CallLog;
import com.iskyun.im.data.resp.ListRecords;

import java.util.List;

public class CallLogViewModel extends BaseListViewModel<ListRecords<CallLog>> {
    private final MutableLiveData<List<CallLog>> liveData;

    public CallLogViewModel(){
        super();
        liveData=new MutableLiveData<>();
    }

    @Override
    protected void getData() {
        getCallLog();
    }

    @Override
    public void onSuccess(ListRecords<CallLog> callLog) {
        if (callLog.getRecords()!=null)
            liveData.postValue(callLog.getRecords());
    }

    public LiveData<List<CallLog>> getCallLog() {
        userRepository.getCallLog(page,PAGE_SIZE).map(this).subscribe(this);
        return liveData;
    }

}