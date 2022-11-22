package com.iskyun.im.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder;
import com.iskyun.im.R;
import com.iskyun.im.data.bean.DynamicTopic;
import com.iskyun.im.databinding.ViewItemDynamicTopicBinding;

/**
 * 话题动态
 */
public class DynamicTopicAdapter extends BaseQuickAdapter<DynamicTopic, BaseDataBindingHolder<ViewItemDynamicTopicBinding>> {

    public DynamicTopicAdapter() {
        super(R.layout.view_item_dynamic_topic);
    }

    @Override
    protected void convert(@NonNull BaseDataBindingHolder<ViewItemDynamicTopicBinding> viewItemDynamicTopicBindingBaseDataBindingHolder, DynamicTopic dynamicTopic) {

    }
}
