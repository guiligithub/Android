package com.iskyun.im.ui.dynamic;

import android.view.LayoutInflater;

import androidx.lifecycle.ViewModelProvider;

import com.iskyun.im.base.BaseActivity;
import com.iskyun.im.databinding.ActivityTopicDynamicBinding;
import com.iskyun.im.ui.dynamic.viewmodel.DynamicViewModel;

/**
 * 话题对应的动态
 */
public class TopicDynamicActivity extends BaseActivity<ActivityTopicDynamicBinding, DynamicViewModel> {

    @Override
    public DynamicViewModel onCreateViewModel(ViewModelProvider provider) {
        return provider.get(DynamicViewModel.class);
    }

    @Override
    public ActivityTopicDynamicBinding onCreateViewBinding(LayoutInflater inflater) {
        return ActivityTopicDynamicBinding.inflate(inflater);
    }

    @Override
    public void initBase() {

    }

    @Override
    protected ToolbarHeader getToolbarHeader() {
        return ToolbarHeader.CustomHeader;
    }

    @Override
    public int getTitleText() {
        return 0;
    }
}
