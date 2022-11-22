package com.iskyun.im.ui.dynamic;

import android.view.LayoutInflater;

import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.iskyun.im.R;
import com.iskyun.im.base.BaseActivity;
import com.iskyun.im.databinding.ActivityTopicBinding;
import com.iskyun.im.ui.dynamic.frag.TopicFragment;
import com.iskyun.im.ui.dynamic.viewmodel.TopicViewModel;


/**
 * 话题
 */
public class TopicActivity extends BaseActivity<ActivityTopicBinding, TopicViewModel> {


    @Override
    public TopicViewModel onCreateViewModel(ViewModelProvider provider) {
        return provider.get(TopicViewModel.class);
    }

    @Override
    public ActivityTopicBinding onCreateViewBinding(LayoutInflater inflater) {
        return ActivityTopicBinding.inflate(inflater);
    }

    @Override
    public void initBase() {
        TopicFragment fragment = new TopicFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, fragment, fragment.getClass().getSimpleName());
        ft.commit();
    }

    @Override
    public int getTitleText() {
        return R.string.topic;
    }
}