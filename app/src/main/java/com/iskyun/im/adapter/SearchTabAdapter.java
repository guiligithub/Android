package com.iskyun.im.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder;
import com.iskyun.im.data.bean.Anchor;
import com.iskyun.im.databinding.ViewItemSearchBinding;
import com.iskyun.im.utils.UserUtils;

public class SearchTabAdapter  extends BaseQuickAdapter<Anchor, BaseDataBindingHolder<ViewItemSearchBinding>> {

    public SearchTabAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NonNull BaseDataBindingHolder<ViewItemSearchBinding> bindingHolder, Anchor anchor) {
        ViewItemSearchBinding tabBinding = bindingHolder.getDataBinding();
        if (tabBinding == null)
            return;
        tabBinding.tabTvName.setText(anchor.getNickname());
        UserUtils.setHeaderUrl(tabBinding.tabIvAvatar, anchor.getSex(), anchor.getHeadUrl());
        UserUtils.setUserSexAndAge(tabBinding.tabTvAge, anchor.getSex(), anchor.getAge());
        tabBinding.tabTvContent.setText(anchor.getSignature());
    }

}
