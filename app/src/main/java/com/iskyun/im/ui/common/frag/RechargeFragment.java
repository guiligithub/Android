package com.iskyun.im.ui.common.frag;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.iskyun.im.R;
import com.iskyun.im.adapter.RechargeAdapter;
import com.iskyun.im.base.BaseFragment;
import com.iskyun.im.databinding.LayoutRecyclerBinding;
import com.iskyun.im.helper.LiveDataBus;
import com.iskyun.im.ui.common.viewmodel.RechargeViewModel;
import com.iskyun.im.widget.GridItemDecoration;

public class RechargeFragment extends BaseFragment<LayoutRecyclerBinding, RechargeViewModel> {

    public static final String SELECTED = "SELECTED";
    public static final String UN_SELECTED = "UN_SELECTED";
    public static final int DIAMOND = 0x01;
    public static final int VIP = 0x02;
    private RechargeAdapter rechargeAdapter;

    public RechargeFragment() {}

    public static RechargeFragment newInstance(int type) {
        RechargeFragment fragment = new RechargeFragment();
        Bundle args = new Bundle();
        args.putInt("type", type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public RechargeViewModel onCreateViewModel(ViewModelProvider provider) {
        return provider.get(RechargeViewModel.class);
    }

    @Override
    public LayoutRecyclerBinding onCreateViewBinding(LayoutInflater inflater) {
        return LayoutRecyclerBinding.inflate(inflater);
    }

    @Override
    public void initBase() {
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
        RecyclerView.ItemDecoration decoration = new GridItemDecoration(getActivity(),
                getResources().getDimensionPixelSize(R.dimen.text_margin), R.color.white);

        mViewBinding.recyclerView.setLayoutManager(layoutManager);
        mViewBinding.recyclerView.addItemDecoration(decoration);
        mViewBinding.recyclerView.setNestedScrollingEnabled(true);
        mViewBinding.recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        rechargeAdapter = new RechargeAdapter();
        mViewBinding.recyclerView.setAdapter(rechargeAdapter);

        rechargeAdapter.setOnItemClickListener((adapter, view, position) -> {
            setSelectedPosition(position);
        });

        LiveDataBus.get().with(UN_SELECTED,String.class).observe(this, s -> {
            setSelectedPosition(RechargeAdapter.DEFAULT_SELECT);
        });

        int type = DIAMOND;
        if (getArguments() != null) {
            type = getArguments().getInt("type");
        }
        if (type == DIAMOND) {
            mViewModel.observeDiamonds().observe(this, diamonds -> {
                rechargeAdapter.setList(diamonds);
            });
        } else {
            mViewModel.observeVips().observe(this, vips -> {
                rechargeAdapter.setList(vips);
            });
        }
    }

    @Override
    public int getTitleText() {
        return 0;
    }

    private void setSelectedPosition(int position){
        rechargeAdapter.selectPosition(position);
        if(position != RechargeAdapter.DEFAULT_SELECT){
            LiveDataBus.get().with(SELECTED).postValue(rechargeAdapter.getItem(position));
        }
    }

}
