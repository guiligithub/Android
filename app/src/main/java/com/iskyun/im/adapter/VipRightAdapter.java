package com.iskyun.im.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder;
import com.iskyun.im.R;
import com.iskyun.im.databinding.ViewItemVipRightBinding;

public class VipRightAdapter extends BaseQuickAdapter<VipRightAdapter.VipRightModel,
        BaseDataBindingHolder<ViewItemVipRightBinding>>  {


    public VipRightAdapter() {
        super(R.layout.view_item_vip_right);
    }

    @Override
    protected void convert(@NonNull BaseDataBindingHolder<ViewItemVipRightBinding> bindingHolder,
                           VipRightModel vipRightModel) {

        ViewItemVipRightBinding binding = bindingHolder.getDataBinding();
        if (binding == null)
            return;
        binding.itemVipRightImage.setImageResource(vipRightModel.image);
        binding.itemVipRightTvName.setTextColor(vipRightModel.titleColor);
        binding.itemVipRightTvName.setText(vipRightModel.title);
        binding.itemVipRightTvContent.setText(vipRightModel.content);
    }

    public static class VipRightModel{

        public VipRightModel(){}

        public VipRightModel(int image, String title, int titleColor,String content){
            this.image = image;
            this.titleColor = titleColor;
            this.title = title;
            this.content = content;
        }

        public int image;
        public String title;
        public int titleColor;
        public String content;
    }
}
