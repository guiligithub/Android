package com.iskyun.im.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder;
import com.iskyun.im.R;
import com.iskyun.im.databinding.ViewItemImageGridBinding;
import com.iskyun.im.utils.glide.ImageLoader;

public class ImageGridAdapter extends BaseQuickAdapter<String, BaseDataBindingHolder<ViewItemImageGridBinding>> {

    public ImageGridAdapter() {
        super(R.layout.view_item_image_grid);
    }

    @Override
    protected void convert(@NonNull BaseDataBindingHolder<ViewItemImageGridBinding> binding, String s) {

        ViewItemImageGridBinding imageBinding = binding.getDataBinding();
        if(imageBinding == null)
            return;
        ImageLoader.get().loadRoundedCorners(imageBinding.viewItemImageGridIv, s);
    }
}
