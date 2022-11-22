package com.iskyun.im.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder;
import com.iskyun.im.R;
import com.iskyun.im.databinding.ViewItemMoreActionBinding;
import com.iskyun.im.ui.frag.MoreActionFragment;
import com.iskyun.im.utils.DisplayUtils;

public class MoreActionAdapter extends BaseQuickAdapter<MoreActionFragment.MoreType,
        BaseDataBindingHolder<ViewItemMoreActionBinding>> {

    public MoreActionAdapter() {
        super(R.layout.view_item_more_action);
    }

    @Override
    protected void convert(@NonNull BaseDataBindingHolder<ViewItemMoreActionBinding> holderBinding,
                           MoreActionFragment.MoreType moreType) {
        ViewItemMoreActionBinding binding = holderBinding.getDataBinding();
        if (binding != null) {
            if (moreType.getContent().equals(MoreActionFragment.MoreType.UN_ATTENTION.getContent())
                    || moreType.getContent().equals(MoreActionFragment.MoreType.UN_BLOCKLIST.getContent())) {
                binding.viewMoreActionTvContent.setTextColor(DisplayUtils.getColor(R.color.colorPrimary));
            }else {
                binding.viewMoreActionTvContent.setTextColor(DisplayUtils.getColor(R.color.colorSubTitle));
            }
            binding.viewMoreActionTvContent.setText(moreType.getContent());
        }
    }
}
