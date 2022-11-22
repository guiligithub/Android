package com.iskyun.im.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder;
import com.iskyun.im.data.bean.Gift;
import com.iskyun.im.data.bean.MyGift;
import com.iskyun.im.databinding.ViewItemMygiftBinding;
import com.iskyun.im.utils.glide.ImageLoader;

public class MyGiftAdapter extends BaseQuickAdapter<Gift, BaseDataBindingHolder<ViewItemMygiftBinding>>  {

    private MyGift myGifts;
    public static final int DEFAULT_SELECT = -1;
    private int selectIndex = DEFAULT_SELECT;

    public MyGiftAdapter(int layoutResId) {
        super(layoutResId);
    }


    @Override
    public void onBindViewHolder(@NonNull BaseDataBindingHolder<ViewItemMygiftBinding> holder, int position) {
        super.onBindViewHolder(holder, position);
        ViewItemMygiftBinding binding=holder.getDataBinding();

        Gift myGift = getItem(position);
            binding.viewItemGitTvName.setText(myGift.getGiftName());
            binding.viewItemGitTvPrice.setText(String.valueOf(myGift.getGiftPrice()));
            binding.viewItemGitTvNum.setText(String.valueOf("数量："+myGift.getGiftNum()));
            ImageLoader.get().loadTransparentImage(binding.viewItemGitIvIcon,  myGift.getGiftPic());
    }

    @Override
    protected void convert(@NonNull BaseDataBindingHolder<ViewItemMygiftBinding> holder, Gift myGift) {
    }

    public void selectPosition(int position) {
        selectIndex = position;
        notifyDataSetChanged();
    }
}
