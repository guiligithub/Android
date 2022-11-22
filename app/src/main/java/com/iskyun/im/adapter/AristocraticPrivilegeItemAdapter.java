package com.iskyun.im.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder;
import com.iskyun.im.data.bean.MineItemModel;
import com.iskyun.im.databinding.ViewArstocraticPrivilegItemBinding;

public class AristocraticPrivilegeItemAdapter extends BaseQuickAdapter<MineItemModel, BaseDataBindingHolder<ViewArstocraticPrivilegItemBinding>> {


    public AristocraticPrivilegeItemAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NonNull BaseDataBindingHolder<ViewArstocraticPrivilegItemBinding> itemHolder,
                           MineItemModel model) {
        ViewArstocraticPrivilegItemBinding binding = itemHolder.getDataBinding();
        if (binding == null)
            return;
        binding.mineItemIvIcon.setImageResource(model.getIcon());
        binding.mineItemTvText.setText(model.getTitle());

    }

}
