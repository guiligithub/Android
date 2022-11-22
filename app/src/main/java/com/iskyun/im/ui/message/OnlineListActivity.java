package com.iskyun.im.ui.message;

import android.view.LayoutInflater;

import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.iskyun.im.R;
import com.iskyun.im.base.BaseActivity;
import com.iskyun.im.databinding.ActivityBlacklistBinding;
import com.iskyun.im.ui.message.frag.OnlineListFragment;
import com.iskyun.im.ui.message.viewmodel.OnlineViewModel;

public class OnlineListActivity extends BaseActivity<ActivityBlacklistBinding, OnlineViewModel> {
    @Override
    public OnlineViewModel onCreateViewModel(ViewModelProvider provider) {
        return provider.get(OnlineViewModel.class);
    }

    @Override
    public ActivityBlacklistBinding onCreateViewBinding(LayoutInflater inflater) {
        return ActivityBlacklistBinding.inflate(inflater);
    }

    @Override
    public void initBase() {
        OnlineListFragment fragment = new OnlineListFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, fragment, fragment.getClass().getSimpleName());
        ft.commit();
    }

    @Override
    public int getTitleText() {
        return R.string.online;
    }
}
