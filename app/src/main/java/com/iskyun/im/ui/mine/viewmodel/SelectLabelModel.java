package com.iskyun.im.ui.mine.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SelectLabelModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public SelectLabelModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is mine fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}