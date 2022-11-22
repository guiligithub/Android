package com.iskyun.im.ui.dynamic.frag;

import android.view.LayoutInflater;

import androidx.lifecycle.ViewModelProvider;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.iskyun.im.adapter.DynamicTopicAdapter;
import com.iskyun.im.base.BaseListFragment;
import com.iskyun.im.databinding.FragmentBlacklistBinding;
import com.iskyun.im.ui.dynamic.viewmodel.TopicViewModel;

public class TopicFragment extends BaseListFragment<FragmentBlacklistBinding, TopicViewModel> {


    @Override
    protected BaseQuickAdapter<?, ?> onCreateAdapter() {
        return new DynamicTopicAdapter();
    }

    @Override
    protected boolean refreshEnable() {
        return false;
    }

    @Override
    public TopicViewModel onCreateViewModel(ViewModelProvider provider) {
        return provider.get(TopicViewModel.class);
    }

    @Override
    public FragmentBlacklistBinding onCreateViewBinding(LayoutInflater inflater) {
        return FragmentBlacklistBinding.inflate(inflater);
    }

    @Override
    public void initBase() {
        super.initBase();

    }

    @Override
    public int getTitleText() {
        return 0;
    }
}
