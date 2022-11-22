package com.iskyun.im.ui.mine;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.gyf.immersionbar.ImmersionBar;
import com.iskyun.im.ImLiveApp;
import com.iskyun.im.R;
import com.iskyun.im.base.BaseActivity;
import com.iskyun.im.data.bean.Sex;
import com.iskyun.im.data.bean.User;
import com.iskyun.im.databinding.ActivityRevenueCenterBinding;
import com.iskyun.im.ui.mine.viewmodel.MineModel;
import com.iskyun.im.utils.ActivityUtils;
import com.iskyun.im.utils.manager.SpManager;

public class RevenueCenterActivity extends BaseActivity<ActivityRevenueCenterBinding, MineModel> implements View.OnClickListener {
    private User user;

    @Override
    public void onClick(View view) {

    }

    @Override
    public MineModel onCreateViewModel(ViewModelProvider provider) {
        return provider.get(MineModel.class);
    }

    @Override
    public ActivityRevenueCenterBinding onCreateViewBinding(LayoutInflater inflater) {
        return ActivityRevenueCenterBinding.inflate(inflater);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImmersionBar.with(this).titleBar(mViewBinding.getRoot().findViewById(R.id.tl_header))
                .init();
    }

    @Override
    public void initBase() {
        user = SpManager.getInstance().getCurrentUser();
        mViewBinding.setUser(user);
        mViewBinding.tlBack.setOnClickListener(view -> finish());

        mViewBinding.revenueCenterTvList.setOnClickListener(v -> ActivityUtils.launcher(ImLiveApp.get().getTopActivity(), RevenueListActivity.class));

        mViewModel.selectBalance().observe(this, balance -> {
            if (user.getSex() == Sex.MAN.ordinal()) {
                user.setUserDiamond((int) balance.getDiamondNum());
            } else {
                if(balance.getBalanceNum() != 0){
                    user.setUserBalance(balance.getBalanceNum());
                }
            }
            SpManager.getInstance().setCurrentUser(user);
        });
    }

    @Override
    public int getTitleText() {
        return 0;
    }

    @Override
    protected ToolbarHeader getToolbarHeader() {
        return ToolbarHeader.CustomHeader;
    }
}
