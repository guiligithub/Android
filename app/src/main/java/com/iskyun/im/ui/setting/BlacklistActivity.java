package com.iskyun.im.ui.setting;

import android.view.LayoutInflater;

import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.iskyun.im.R;
import com.iskyun.im.base.BaseActivity;
import com.iskyun.im.databinding.ActivityBlacklistBinding;
import com.iskyun.im.ui.setting.frag.BlacklistFragment;
import com.iskyun.im.viewmodel.RelationViewModel;

public class BlacklistActivity extends BaseActivity<ActivityBlacklistBinding, RelationViewModel> {


    @Override
    public RelationViewModel onCreateViewModel(ViewModelProvider provider) {
        return provider.get(RelationViewModel.class);
    }

    @Override
    public ActivityBlacklistBinding onCreateViewBinding(LayoutInflater inflater) {
        return ActivityBlacklistBinding.inflate(inflater);
    }

    @Override
    public void initBase() {
        BlacklistFragment fragment = new BlacklistFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, fragment, fragment.getClass().getSimpleName());
        ft.commit();
    }

    @Override
    public int getTitleText() {
        return R.string.blacklist;
    }
}