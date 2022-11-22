package com.iskyun.im.adapter;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.BaseLoadMoreModule;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder;
import com.iskyun.im.R;
import com.iskyun.im.data.bean.Anchor;
import com.iskyun.im.data.bean.OnlineStatus;
import com.iskyun.im.data.bean.Sex;
import com.iskyun.im.databinding.ViewItemHomeTabBinding;
import com.iskyun.im.utils.UserUtils;
import com.iskyun.im.utils.glide.ImageLoader;

public class HomeTabAdapter extends BaseQuickAdapter<Anchor, BaseDataBindingHolder<ViewItemHomeTabBinding>>
        implements LoadMoreModule {

    public HomeTabAdapter(@LayoutRes int layoutResId) {
        super(layoutResId);
    }


    @Override
    protected void convert(@NonNull BaseDataBindingHolder<ViewItemHomeTabBinding>
                                   holder, Anchor anchor) {
        ViewItemHomeTabBinding binding = holder.getDataBinding();
        if (binding == null)
            return;
        if (TextUtils.isEmpty(anchor.getHeadUrl())) {
            if (anchor.getSex() == Sex.WOMAN.ordinal()) {
                ImageLoader.get().load(binding.tabItemIvAvatar, R.mipmap.icon_recommend_woman);
            } else {
                ImageLoader.get().load(binding.tabItemIvAvatar, R.mipmap.icon_recommend_man);
            }

            UserUtils.setDefaultHeaderUrl(binding.tabIvAvatar, anchor.getSex());
        } else {
            ImageLoader.get().load(binding.tabItemIvAvatar, anchor.getHeadUrl());
        }

        if (anchor.getPhotoUrls().size() != 0) {
            ImageLoader.get().loadCropCircle(binding.tabItemIvAvatar, anchor.getPhotoUrls().get(0));
        }


        if (anchor.getSex() == Sex.WOMAN.ordinal()) {
            binding.tabIvVideo.setVisibility(View.VISIBLE);
            binding.tabTvPrice.setText(String.format(getContext().getString(R.string.chat_price), anchor.getVideoMinute()));
            binding.tabTvPrice.setVisibility(View.VISIBLE);
            binding.tabTvLevel.setVisibility(View.GONE);
        } else {
            binding.tabTvPrice.setText("");
            binding.tabTvPrice.setVisibility(View.GONE);
            binding.tabIvVideo.setVisibility(View.INVISIBLE);

            binding.tabTvLevel.setVisibility(View.VISIBLE);
            if (anchor.getLevel() > 0) {
                binding.tabTvLevel.setText("Lv" + anchor.getLevel());
            }
        }

        if (anchor.isOnline() == OnlineStatus.ONLINE.getStatus()) {
            binding.tabIvOnlineStatus.setImageResource(R.mipmap.icon_anchor_status_on);
        } else {
            binding.tabIvOnlineStatus.setImageResource(R.mipmap.icon_anchor_status_off);
        }

        binding.tabTvName.setText(anchor.getNickname());
        if(anchor.getStarLevel() != null){
            binding.tabRsvStar.setStarNum(anchor.getStarLevel());
        }else {
            binding.tabRsvStar.setStarNum(3);
        }
    }

    /**
     * 瀑布流设置高度
     *
     * @param view
     */
    private void setLayoutParams(View view) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        view.setLayoutParams(params);
    }

    @NonNull
    @Override
    public BaseLoadMoreModule addLoadMoreModule(@NonNull BaseQuickAdapter<?, ?> baseQuickAdapter) {
        return new BaseLoadMoreModule(this);
    }
}
