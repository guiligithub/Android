package com.iskyun.im.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder;
import com.iskyun.im.R;
import com.iskyun.im.data.bean.Blacklist;
import com.iskyun.im.databinding.ViewItemBlacklistBinding;
import com.iskyun.im.utils.UserUtils;

public class BlacklistAdapter extends BaseQuickAdapter<Blacklist, BaseDataBindingHolder<ViewItemBlacklistBinding>> {

    public BlacklistAdapter() {
        super(R.layout.view_item_blacklist);
    }


    @Override
    protected void convert(@NonNull BaseDataBindingHolder<ViewItemBlacklistBinding> bindingHolder, Blacklist blacklist) {
        ViewItemBlacklistBinding binding = bindingHolder.getDataBinding();
        if (binding == null)
            return;
        binding.setBlacklist(blacklist);

        UserUtils.setUserSexAndAge(binding.viewItemBlacklistTvAge, blacklist.getSex(), blacklist.getAge());
        UserUtils.setHeaderUrl(binding.viewItemBlacklistIvHeader, blacklist.getSex(), blacklist.getHeadUrl());
    }
}
