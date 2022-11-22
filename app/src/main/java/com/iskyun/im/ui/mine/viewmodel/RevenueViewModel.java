package com.iskyun.im.ui.mine.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.iskyun.im.base.BaseListViewModel;
import com.iskyun.im.data.bean.Revenue;
import com.iskyun.im.data.resp.ListRecords;

import java.util.List;

public class RevenueViewModel extends BaseListViewModel<ListRecords<Revenue>> {
    private final MutableLiveData<List<Revenue>> liveData = new MutableLiveData<>();

    @Override
    protected void getData() {
        getRevenueList();
    }

    @Override
    public void onSuccess(ListRecords<Revenue> revenues) {
        if (revenues.getRecords() != null)
            liveData.postValue(revenues.getRecords());
    }

    public LiveData<List<Revenue>> getRevenueList() {
        showDialog = false;
        userRepository.getRevenueList(page, PAGE_SIZE).map(this).subscribe(this);
        return liveData;
    }
}
