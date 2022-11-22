package com.iskyun.im.ui.message.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.iskyun.im.base.BaseListViewModel;
import com.iskyun.im.base.BaseViewModel;
import com.iskyun.im.data.bean.Banner;
import com.iskyun.im.data.bean.CallLog;
import com.iskyun.im.data.bean.SysMessage;
import com.iskyun.im.data.resp.ListRecords;

import java.util.List;

public class SystemMessageViewModel extends BaseViewModel<List<SysMessage>> {
    private final MutableLiveData<List<SysMessage>> sysMessagesLiveData;

    public SystemMessageViewModel(){
        super();
        sysMessagesLiveData=new MutableLiveData<>();
    }

    @Override
    public void onSuccess(List<SysMessage> sysMessages) {
        sysMessagesLiveData.postValue(sysMessages);
    }

    public LiveData<List<SysMessage>> getSysMessage(){
        userRepository.getSysMessage().map(this).subscribe(this);
        return sysMessagesLiveData;
    }

}
