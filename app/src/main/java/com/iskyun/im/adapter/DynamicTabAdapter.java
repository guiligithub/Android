package com.iskyun.im.adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.BaseLoadMoreModule;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder;
import com.iskyun.im.R;
import com.iskyun.im.data.bean.Dynamic;
import com.iskyun.im.databinding.ViewItemDynamicTabBinding;
import com.iskyun.im.utils.DateUtils;
import com.iskyun.im.utils.DisplayUtils;
import com.iskyun.im.utils.UserUtils;

public class DynamicTabAdapter extends BaseQuickAdapter<Dynamic, BaseDataBindingHolder<ViewItemDynamicTabBinding>>
        implements LoadMoreModule {

    public DynamicTabAdapter() {
        super(R.layout.view_item_dynamic_tab);
    }

    @Override
    protected void convert(@NonNull BaseDataBindingHolder<ViewItemDynamicTabBinding>
                                   holder, Dynamic dynamic) {
        ViewItemDynamicTabBinding tabBinding = holder.getDataBinding();
        if (tabBinding == null)
            return;
        UserUtils.setHeaderUrl(tabBinding.tabIvAvatar,dynamic.getSex(), dynamic.getHeadUrl());
        UserUtils.setUserSexAndAge(tabBinding.anchorDetailTvAge,dynamic.getSex(),dynamic.getAge());

        if (dynamic.isAttention()) {
            tabBinding.tabIvFollow.setImageResource(R.mipmap.icon_followed_bg);
        } else {
            tabBinding.tabIvFollow.setImageResource(R.mipmap.icon_follow_bg);
        }

        int resCommendId;
        if (dynamic.isCommend()) {
            resCommendId = R.mipmap.icon_cardioid_selected;
        } else {
            resCommendId = R.mipmap.icon_cardioid;
        }
        tabBinding.tabTvLike.setCompoundDrawablesWithIntrinsicBounds(
                DisplayUtils.getDrawable(resCommendId), null, null, null);
        tabBinding.tabTvLike.setText(String.valueOf(dynamic.getCommendNum()));
        tabBinding.tabTvTime.setText(DateUtils.getRelativeTimeSpanString(dynamic.getCreateTime()));
        tabBinding.tabTvName.setText(dynamic.getNickname() /*+ getItemPosition(dynamic)*/);
        tabBinding.tabTvContent.setText(dynamic.getTextDetails());

        if (!dynamic.getAppCommonFileList().isEmpty()) {
            tabBinding.imagerecycler.setVisibility(View.VISIBLE);
            if (tabBinding.imagerecycler.getAdapter() == null) {
                GridLayoutManager manager = new GridLayoutManager(getContext(), 2);
                manager.setAutoMeasureEnabled(true);
                tabBinding.imagerecycler.setLayoutManager(manager);
                /*GridItemDecoration decoration = new GridItemDecoration(ImLiveApp.get().getTopActivity(),
                        DisplayUtils.dp2px(3), R.color.white);
                tabBinding.imagerecycler.addItemDecoration(decoration);*/
                NearbyAdapter nearbyadapter = new NearbyAdapter();
                tabBinding.imagerecycler.setAdapter(nearbyadapter);
                nearbyadapter.updateData(dynamic.getAppCommonFileList(), dynamic.getFileType());
            } else {
                NearbyAdapter nearbyAdapter = (NearbyAdapter) tabBinding.imagerecycler.getAdapter();
                nearbyAdapter.updateData(dynamic.getAppCommonFileList(), dynamic.getFileType());
            }
        } else {
            tabBinding.imagerecycler.setVisibility(View.GONE);
        }
    }

    @Override
    public BaseLoadMoreModule addLoadMoreModule(@NonNull BaseQuickAdapter<?, ?> baseQuickAdapter) {
        return new BaseLoadMoreModule(this);
    }

}

