package com.iskyun.im.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder;
import com.iskyun.im.R;
import com.iskyun.im.databinding.ViewItemImageGridBinding;
import com.iskyun.im.databinding.ViewItemMineImageGridBinding;
import com.iskyun.im.utils.glide.ImageLoader;

public class MineImageGridAdapter extends BaseQuickAdapter<String, BaseDataBindingHolder<ViewItemMineImageGridBinding>> {

    public MineImageGridAdapter() {
        super(R.layout.view_item_mine_image_grid);
    }

    @Override
    protected void convert(@NonNull BaseDataBindingHolder<ViewItemMineImageGridBinding> binding, String s) {

        ViewItemMineImageGridBinding imageBinding = binding.getDataBinding();
        if(imageBinding == null)
            return;
        ImageLoader.get().loadRoundedCorners(imageBinding.viewItemImageGridIv, s);
    }
}
