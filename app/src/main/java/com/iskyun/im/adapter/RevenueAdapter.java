package com.iskyun.im.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.BaseLoadMoreModule;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder;
import com.iskyun.im.R;
import com.iskyun.im.data.bean.Revenue;
import com.iskyun.im.data.bean.Sex;
import com.iskyun.im.databinding.ViewItemRevenueDetailBinding;
import com.iskyun.im.utils.DateUtils;
import com.iskyun.im.utils.DisplayUtils;
import com.iskyun.im.utils.UserUtils;

public class RevenueAdapter extends BaseQuickAdapter<Revenue, BaseDataBindingHolder<ViewItemRevenueDetailBinding>> implements LoadMoreModule {

    public RevenueAdapter() {
        super(R.layout.view_item_revenue_detail);
    }

    @NonNull
    @Override
    public BaseLoadMoreModule addLoadMoreModule(@NonNull BaseQuickAdapter<?, ?> baseQuickAdapter) {
        return new BaseLoadMoreModule(this);
    }

    @Override
    protected void convert(@NonNull BaseDataBindingHolder<ViewItemRevenueDetailBinding> bindingHolder, Revenue revenue) {

        ViewItemRevenueDetailBinding binding = bindingHolder.getDataBinding();
        if (binding == null)
            return;

        UserUtils.setHeaderUrl(binding.viewItemRevenueIvHeader, Sex.MAN.ordinal(), revenue.getHeadUrl());

        binding.viewItemRevenueTvRevenue.setText(revenue.getEarnings()+ DisplayUtils.getString(R.string.yb));
        binding.viewItemRevenueTvTime.setText(DateUtils.getRelativeTimeSpanString(revenue.getCreateTime()));

        String typeContent = getContext().getString(R.string.chat);
        if(revenue.getEarningsType() == 6){
            typeContent =  getContext().getString(R.string.gift);
        } else if(revenue.getEarningsType() == 5){
            typeContent =  getContext().getString(R.string.look_wx);
        } else if(revenue.getEarningsType() == 4){
            typeContent =  getContext().getString(R.string.video)+typeContent;
        } else if(revenue.getEarningsType() == 3){
            typeContent =  getContext().getString(R.string.voice)+typeContent;
        }
        binding.viewItemRevenueTvTypeContent.setText(typeContent);
    }
}
