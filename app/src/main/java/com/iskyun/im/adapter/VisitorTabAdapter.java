package com.iskyun.im.adapter;

import android.content.Context;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.BaseLoadMoreModule;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder;
import com.iskyun.im.data.bean.UserRelations;
import com.iskyun.im.databinding.FragmentBroeswVisitorItemBinding;
import com.iskyun.im.utils.DateUtils;
import com.iskyun.im.utils.UserUtils;

import java.util.List;

public class VisitorTabAdapter extends BaseQuickAdapter<UserRelations, BaseDataBindingHolder<FragmentBroeswVisitorItemBinding>> implements LoadMoreModule {
    private Context context;
    private List<UserRelations> userRelations;

    public VisitorTabAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NonNull BaseDataBindingHolder<FragmentBroeswVisitorItemBinding> holder, UserRelations userRelations) {
        FragmentBroeswVisitorItemBinding tabBinding = holder.getDataBinding();
        if (tabBinding == null)
            return;
        tabBinding.tabTvName.setText(userRelations.getNickname());
        UserUtils.setHeaderUrl(tabBinding.tabIvAvatar,userRelations.getSex(),userRelations.getHeadUrl());
        UserUtils.setUserSexAndAge(tabBinding.anchorDetailTvAge,userRelations.getSex(),userRelations.getAge());
        tabBinding.tabTvContent.setText(userRelations.getSignature());
        tabBinding.tabIvFollow.setText(DateUtils.getRelativeTimeSpanString(userRelations.getCreateTime()));
    }

    @NonNull
    @Override
    public BaseLoadMoreModule addLoadMoreModule(@NonNull BaseQuickAdapter<?, ?> baseQuickAdapter) {
        return new BaseLoadMoreModule(this);
    }
}