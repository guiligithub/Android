package com.iskyun.im.adapter;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.BaseLoadMoreModule;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder;
import com.iskyun.im.R;
import com.iskyun.im.data.bean.Sex;
import com.iskyun.im.data.bean.SysMessage;
import com.iskyun.im.data.bean.UserRelations;
import com.iskyun.im.databinding.FragmentBroeswVisitorItemBinding;
import com.iskyun.im.databinding.ViewItemSystemMessageBinding;
import com.iskyun.im.utils.DisplayUtils;
import com.iskyun.im.utils.glide.ImageLoader;

import java.util.List;

public class SystemMessageAdapter extends BaseQuickAdapter<SysMessage, BaseDataBindingHolder<ViewItemSystemMessageBinding>> {

    public SystemMessageAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NonNull BaseDataBindingHolder<ViewItemSystemMessageBinding> holder, SysMessage sysMessage) {
        ViewItemSystemMessageBinding tabBinding = holder.getDataBinding();
        if (tabBinding == null)
            return;
        tabBinding.sysMessageDataTv.setText(sysMessage.getCreateTime());
        tabBinding.sysMessageTv.setText(sysMessage.getMessagesContent());
    }

}
