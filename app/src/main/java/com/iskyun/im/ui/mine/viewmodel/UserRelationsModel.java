package com.iskyun.im.ui.mine.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.iskyun.im.base.BaseListViewModel;
import com.iskyun.im.data.bean.RelationType;
import com.iskyun.im.data.bean.UserRelations;
import com.iskyun.im.data.resp.ListRecords;

import java.util.List;

public class UserRelationsModel extends BaseListViewModel<ListRecords<UserRelations>> {
    private final MutableLiveData<List<UserRelations>> liveData;
    private RelationType relationType;

    public UserRelationsModel() {
        super();
        liveData = new MutableLiveData<>();
    }

    @Override
    protected void getData() {
        getUserRelations(this.relationType);
    }

    @Override
    public void onSuccess(ListRecords<UserRelations> userRelations) {
        if (userRelations.getRecords() != null)
            liveData.postValue(userRelations.getRecords());
    }

    public LiveData<List<UserRelations>> getUserRelations(RelationType relationType) {
        if(this.relationType == null){
            this.relationType = relationType;
        }
        userRepository.getUserRelations(relationType, page, PAGE_SIZE).map(this).subscribe(this);
        return liveData;
    }

}