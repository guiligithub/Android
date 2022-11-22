package com.iskyun.im.adapter;

import android.content.Context;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.BaseLoadMoreModule;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder;
import com.iskyun.im.data.bean.UserRelations;
import com.iskyun.im.databinding.FragmentBroeswFollowTabBinding;
import com.iskyun.im.utils.UserUtils;

import java.util.List;

public class FollowTabAdapter extends BaseQuickAdapter<UserRelations, BaseDataBindingHolder<FragmentBroeswFollowTabBinding>> implements LoadMoreModule {
    private Context context;
    private List<UserRelations> userRelations;

    public FollowTabAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NonNull BaseDataBindingHolder<FragmentBroeswFollowTabBinding> holder, UserRelations userRelations) {
        FragmentBroeswFollowTabBinding tabBinding = holder.getDataBinding();
        if (tabBinding == null)
            return;
        tabBinding.tabTvName.setText(userRelations.getNickname());
        UserUtils.setHeaderUrl(tabBinding.tabIvAvatar,userRelations.getSex(),userRelations.getHeadUrl());
        UserUtils.setUserSexAndAge(tabBinding.anchorDetailTvAge,userRelations.getSex(),userRelations.getAge());
        tabBinding.tabTvContent.setText(userRelations.getSignature());
    }

    @NonNull
    @Override
    public BaseLoadMoreModule addLoadMoreModule(@NonNull BaseQuickAdapter<?, ?> baseQuickAdapter) {
        return new BaseLoadMoreModule(this);
    }
}
