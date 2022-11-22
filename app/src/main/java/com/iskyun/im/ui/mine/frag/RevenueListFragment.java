package com.iskyun.im.ui.mine.frag;

import android.view.LayoutInflater;

import androidx.lifecycle.ViewModelProvider;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder;
import com.iskyun.im.adapter.RevenueAdapter;
import com.iskyun.im.base.BaseListFragment;
import com.iskyun.im.data.bean.Revenue;
import com.iskyun.im.databinding.FragmentBlacklistBinding;
import com.iskyun.im.databinding.ViewItemRevenueDetailBinding;
import com.iskyun.im.ui.mine.viewmodel.RevenueViewModel;

import java.util.ArrayList;
import java.util.List;

public class RevenueListFragment extends BaseListFragment<FragmentBlacklistBinding, RevenueViewModel> {

    private List<Revenue> revenueData = new ArrayList<>();

    @Override
    protected BaseQuickAdapter<Revenue, BaseDataBindingHolder<ViewItemRevenueDetailBinding>> onCreateAdapter() {
        return new RevenueAdapter();
    }

    @Override
    protected boolean refreshEnable() {
        return true;
    }

    @Override
    public RevenueViewModel onCreateViewModel(ViewModelProvider provider) {
        return provider.get(RevenueViewModel.class);
    }

    @Override
    public FragmentBlacklistBinding onCreateViewBinding(LayoutInflater inflater) {
        return FragmentBlacklistBinding.inflate(inflater);
    }

    @Override
    public void initBase() {
        super.initBase();
        refreshLayout.setRefreshing(true);
        mViewModel.getRevenueList().observe(this, revenues -> {
            if (mViewModel.isRefresh()) {
                revenueData.clear();
            }
            revenueData.addAll(revenues);
            ((RevenueAdapter) mAdapter).setList(revenueData);
        });
    }

    @Override
    public int getTitleText() {
        return 0;
    }
}
