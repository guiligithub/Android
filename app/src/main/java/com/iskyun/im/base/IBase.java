package com.iskyun.im.base;

import android.view.LayoutInflater;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewbinding.ViewBinding;

public interface IBase<VB extends ViewBinding,VM extends ViewModel> {

    VM onCreateViewModel(ViewModelProvider provider);

    VB onCreateViewBinding(LayoutInflater inflater);

    void initBase();

    int getTitleText();
}
