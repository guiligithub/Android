package com.iskyun.im.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder;
import com.iskyun.im.R;
import com.iskyun.im.data.bean.Gift;
import com.iskyun.im.databinding.ViewItemGiftBinding;
import com.iskyun.im.ui.frag.GiftFragment;
import com.iskyun.im.utils.DisplayUtils;
import com.iskyun.im.utils.glide.ImageLoader;

public class GiftAdapter extends BaseQuickAdapter<Gift, BaseDataBindingHolder<ViewItemGiftBinding>> {

    public static final int DEFAULT_SELECT_INDEX = -1;
    private int selectIndex = DEFAULT_SELECT_INDEX;

    public GiftAdapter() {
        super(R.layout.view_item_gift);
    }

    public void selectPosition(int position) {
        selectIndex = position;
        notifyDataSetChanged();
    }

    public Gift getSelectGift() {
        if (selectIndex != DEFAULT_SELECT_INDEX) {
            return getItem(selectIndex);
        }
        return null;
    }

    @Override
    protected void convert(@NonNull BaseDataBindingHolder<ViewItemGiftBinding> holder, Gift gift) {

        ViewItemGiftBinding binding = holder.getDataBinding();
        if (binding == null)
            return;
        binding.viewItemGiftView.setSelected(getItemPosition(gift) == selectIndex);
        binding.viewItemGitTvPrice.setText(String.valueOf(gift.getGiftPrice()));

        binding.viewItemGitTvName.setText(gift.getGiftName());
        setImageLayoutParams(binding.viewItemGitIvIcon);

//        Glide.with(binding.viewItemGitIvIcon.getContext()).asBitmap()
//                .load(giftUrl).into(binding.viewItemGitIvIcon);

        ImageLoader.get().loadTransparentImage(binding.viewItemGitIvIcon, gift.getGiftPic());
    }

    private void setImageLayoutParams(View view) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        int spanCount = GiftFragment.NUM_COLUMNS;
        int viewWidth = DisplayUtils.getWidthPixels() - spanCount * DisplayUtils.getDimensionPixelSize(R.dimen.content_margin);
        params.width = viewWidth / spanCount;
        params.height = DisplayUtils.dp2px(80);
        view.setLayoutParams(params);
    }

}
