package com.iskyun.im.ui.home.frag;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder;
import com.iskyun.im.R;
import com.iskyun.im.adapter.HomeTabAdapter;
import com.iskyun.im.adapter.MineBannerAdapter;
import com.iskyun.im.base.BaseListFragment;
import com.iskyun.im.data.bean.Anchor;
import com.iskyun.im.data.bean.Banner;
import com.iskyun.im.databinding.FragmentHomeTabBinding;
import com.iskyun.im.databinding.ViewItemHomeTabBinding;
import com.iskyun.im.ui.home.AnchorInfoActivity;
import com.iskyun.im.ui.home.HomeViewModel;
import com.iskyun.im.ui.login.AppWebActivity;
import com.iskyun.im.utils.ToastUtils;
import com.mobile.auth.gatewayauth.Constant;
import com.youth.banner.indicator.CircleIndicator;

import java.util.ArrayList;
import java.util.List;

public class HomeTabFragment extends BaseListFragment<FragmentHomeTabBinding, HomeViewModel> {

    private static final String TAG = "tag";
    private HomeTabAdapter homeTabAdapter;
    private List<Anchor> data;
    private int tag;

    private MineBannerAdapter bannerAdapter;

    public HomeTabFragment() {
    }

    public static HomeTabFragment newInstance(int tag) {
        HomeTabFragment fragment = new HomeTabFragment();
        Bundle args = new Bundle();
        args.putInt(TAG, tag);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public HomeViewModel onCreateViewModel(ViewModelProvider provider) {
        return provider.get(HomeViewModel.class);
    }

    @Override
    public FragmentHomeTabBinding onCreateViewBinding(LayoutInflater inflater) {
        return FragmentHomeTabBinding.inflate(inflater);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            tag = savedInstanceState.getInt(TAG);
        }
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(TAG, tag);
    }

    @Override
    public void initBase() {
        super.initBase();
        if (getArguments() != null) {
            tag = getArguments().getInt(TAG);
        }
        data = new ArrayList<>();
        refreshLayout.setRefreshing(true);
        mViewModel.getAnchors(tag).observe(this, anchors -> {
            if (mViewModel.isRefresh()) {
                data.clear();
            }
            data.addAll(anchors);
            homeTabAdapter.setList(data);
        });

        homeTabAdapter.setOnItemClickListener((adapter, view, position) -> {
            Anchor anchor = (Anchor) adapter.getData().get(position);
            AnchorInfoActivity.launcher(anchor.getId(), anchor.getSex());
        });


        mViewBinding.banner.setVisibility(tag == 1 ? View.VISIBLE : View.GONE);
        mViewBinding.tabBar.setVisibility(View.GONE);
        if (tag == 1) {
            getBanners();
            bannerAdapter = new MineBannerAdapter(this.getContext(), new ArrayList<>());
            mViewBinding.banner.setAdapter(bannerAdapter);
            //是否允许自动轮播
            mViewBinding.banner.isAutoLoop(true);
            //设置指示器， CircleIndicator为已经定义好的类，直接用就好
            mViewBinding.banner.setIndicator(new CircleIndicator(this.getContext()));
            //设置为 平滑模式  参数 1.左显示 10dp  2.右显示10dp 3.和三图之间的间隙10dp
            mViewBinding.banner.setBannerGalleryEffect(5, 5, 5);
            //开始轮播
            mViewBinding.banner.start();

            mViewBinding.banner.setOnBannerListener((data, position) -> {
                Banner banner = (Banner) data;
                startAppWeb(banner.getPointTo(), "");
            });
        }
        mViewBinding.familyHall.setOnClickListener(this::onClick);
        mViewBinding.OffOrderSquare.setOnClickListener(this::onClick);
        mViewBinding.videoAppointment.setOnClickListener(this::onClick);
        mViewBinding.videoSpeedMatching.setOnClickListener(this::onClick);


    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.family_hall:
            case R.id.Off_order_square:
            case R.id.video_appointment:
            case R.id.video_speed_matching:
                ToastUtils.showToast("正在开发中");
                //GifPlayActivity.launcher("");
                break;
        }

    }

    /**
     * 轮播图
     */

    private void getBanners() {
        mViewModel.getBanners().observe(this, banners -> {
            if (!banners.isEmpty()) {
                mViewBinding.banner.setVisibility(View.VISIBLE);
            }
            bannerAdapter.setDatas(banners);
            bannerAdapter.notifyItemChanged(0, banners.size());
        });
    }

    private void startAppWeb(String url, String title) {
        if(TextUtils.isEmpty(url)){
            return;
        }
        Intent intent = new Intent();
        intent.setClass(getContext(), AppWebActivity.class);
        intent.putExtra(Constant.PROTOCOL_WEBVIEW_URL, url);
        intent.putExtra(Constant.PROTOCOL_WEBVIEW_NAME, title);
        startActivity(intent);
    }

    @Override
    public int getTitleText() {
        return 0;
    }

    @Override
    protected BaseQuickAdapter<Anchor, BaseDataBindingHolder<ViewItemHomeTabBinding>> onCreateAdapter() {
        homeTabAdapter = new HomeTabAdapter(R.layout.view_item_home_tab);
        return homeTabAdapter;
    }

    @Override
    protected boolean refreshEnable() {
        return true;
    }
}