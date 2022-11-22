package com.iskyun.im.adapter;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder;
import com.iskyun.im.R;
import com.iskyun.im.data.bean.Sex;
import com.iskyun.im.data.bean.User;
import com.iskyun.im.databinding.ViewItemCircularImageGridBinding;
import com.iskyun.im.utils.glide.ImageLoader;
import com.iskyun.im.utils.manager.SpManager;

public class CircularImageGridAdapter extends BaseQuickAdapter<String, BaseDataBindingHolder<ViewItemCircularImageGridBinding>> {

    public CircularImageGridAdapter() {
        super(R.layout.view_item_circular_image_grid);
    }

    @Override
    protected void convert(@NonNull BaseDataBindingHolder<ViewItemCircularImageGridBinding> binding, String s) {

        ViewItemCircularImageGridBinding imageBinding = binding.getDataBinding();
        if (imageBinding == null)
            return;
        User user = SpManager.getInstance().getCurrentUser();
        if (TextUtils.isEmpty(s)) {
            if (user.getSex() == Sex.WOMAN.ordinal()) {
                ImageLoader.get().loadCropCircle(imageBinding.viewItemImageGridIv, R.mipmap.icon_recommend_man);
            } else {
                ImageLoader.get().loadCropCircle(imageBinding.viewItemImageGridIv, R.mipmap.icon_recommend_woman);
            }
        } else {
            ImageLoader.get().loadCropCircle(imageBinding.viewItemImageGridIv, s);
        }
        //ImageLoader.get().loadRoundedCorners(imageBinding.viewItemImageGridIv, s);
    }
}
