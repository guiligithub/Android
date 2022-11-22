package com.iskyun.im.ui.common;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.gyf.immersionbar.ImmersionBar;
import com.iskyun.im.R;
import com.iskyun.im.adapter.GuideAdapter;
import com.iskyun.im.base.BaseActivity;
import com.iskyun.im.data.bean.Guide;
import com.iskyun.im.databinding.ActivityGuideBinding;
import com.youth.banner.indicator.CircleIndicator;

import java.util.ArrayList;
import java.util.List;

/**
 * 引导页
 */
public class GuideActivity extends BaseActivity<ActivityGuideBinding, ViewModel> {
    private GuideAdapter bannerAdapter;
    private List<Guide> guideList;

    @Override
    public ViewModel onCreateViewModel(ViewModelProvider provider) {
        return null;
    }

    @Override
    public ActivityGuideBinding onCreateViewBinding(LayoutInflater inflater) {
        return ActivityGuideBinding.inflate(inflater);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImmersionBar.with(this).titleBar(mViewBinding.getRoot().findViewById(R.id.tl_header))
                .init();
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void initBase() {
        getData();
        mViewBinding.introductoryBanner.setVisibility(View.VISIBLE);
        bannerAdapter = new GuideAdapter(this, new ArrayList<>());
        mViewBinding.introductoryBanner.setAdapter(bannerAdapter);
        mViewBinding.introductoryBanner.setDatas(guideList);
        //是否允许自动轮播
        mViewBinding.introductoryBanner.isAutoLoop(false);
        //设置指示器， CircleIndicator为已经定义好的类，直接用就好
        mViewBinding.introductoryBanner.setIndicator(new CircleIndicator(this));
        bannerAdapter.getItemId(R.id.introducttory_btn);
        //开始轮播
        mViewBinding.introductoryBanner.start();

    }

    private void getData() {
        guideList = new ArrayList<>();
        guideList.add(new Guide(R.mipmap.icon_introducttory_one, "一段美好的故事就从现在开始"));
        guideList.add(new Guide(R.mipmap.icon_introducttory_two, "你与女神之间可以尽情的畅聊"));
        guideList.add(new Guide(R.mipmap.icon_introducttory_three, "拉近你与女神之间的一步之遥"));
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
