package com.iskyun.im.adapter;

import android.content.Context;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.BaseLoadMoreModule;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder;
import com.iskyun.im.R;
import com.iskyun.im.data.bean.UserRelations;
import com.iskyun.im.databinding.FragmentBroeswFansItemBinding;
import com.iskyun.im.utils.UserUtils;

import java.util.List;

public class FansTabAdapter extends BaseQuickAdapter<UserRelations, BaseDataBindingHolder<FragmentBroeswFansItemBinding>> implements LoadMoreModule {
    private Context context;
    private List<UserRelations> userRelations;

    public FansTabAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NonNull BaseDataBindingHolder<FragmentBroeswFansItemBinding> holder, UserRelations userRelations) {
        FragmentBroeswFansItemBinding tabBinding = holder.getDataBinding();
        if (tabBinding == null)
            return;
        UserUtils.setHeaderUrl(tabBinding.tabIvAvatar,userRelations.getSex(),userRelations.getHeadUrl());
        UserUtils.setUserSexAndAge(tabBinding.fansDetailTvAge,userRelations.getSex(),userRelations.getAge());
        if (userRelations.isAttention()) {
            tabBinding.tabIvFollow.setImageResource(R.mipmap.icon_followed_bg);
        } else {
            tabBinding.tabIvFollow.setImageResource(R.mipmap.icon_follow_bg);
        }
        tabBinding.tabTvName.setText(userRelations.getNickname());
        tabBinding.tabTvContent.setText(userRelations.getSignature());

    }

    @NonNull
    @Override
    public BaseLoadMoreModule addLoadMoreModule(@NonNull BaseQuickAdapter<?, ?> baseQuickAdapter) {
        return new BaseLoadMoreModule(this);
    }
}
