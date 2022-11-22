package com.iskyun.im.ui.mine;

import android.view.LayoutInflater;

import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.iskyun.im.R;
import com.iskyun.im.base.BaseActivity;
import com.iskyun.im.databinding.ActivityBlacklistBinding;
import com.iskyun.im.ui.mine.frag.RevenueListFragment;
import com.iskyun.im.ui.mine.viewmodel.MineModel;

public class RevenueListActivity extends BaseActivity<ActivityBlacklistBinding, MineModel> {
    @Override
    public MineModel onCreateViewModel(ViewModelProvider provider) {
        return null;
    }

    @Override
    public ActivityBlacklistBinding onCreateViewBinding(LayoutInflater inflater) {
        return ActivityBlacklistBinding.inflate(inflater);
    }

    @Override
    public void initBase() {
        RevenueListFragment fragment = new RevenueListFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, fragment, fragment.getClass().getSimpleName());
        ft.commit();
    }

    @Override
    public int getTitleText() {
        return R.string.revenue_list;
    }
}
