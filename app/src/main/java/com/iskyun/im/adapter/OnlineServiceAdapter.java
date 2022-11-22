package com.iskyun.im.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder;
import com.iskyun.im.R;
import com.iskyun.im.data.bean.OnlineService;
import com.iskyun.im.databinding.ViewItemOnlineBinding;

public class OnlineServiceAdapter extends BaseQuickAdapter<OnlineService,
        BaseDataBindingHolder<ViewItemOnlineBinding>> {

    public OnlineServiceAdapter() {
        super(R.layout.view_item_online);
    }


    @Override
    protected void convert(@NonNull BaseDataBindingHolder<ViewItemOnlineBinding> bindingHolder, OnlineService onlineService) {
        ViewItemOnlineBinding binding = bindingHolder.getDataBinding();
        if (binding == null)
            return;

        binding.setOnlineService(onlineService);
        String type = "";
        int typeResId = R.mipmap.icon_qq;
        if(onlineService.getAccountType() == 1){
            type = "QQ";
            typeResId =  R.mipmap.icon_qq;
        } else if(onlineService.getAccountType() == 2){
            type = getContext().getString(R.string.we_chai);
            typeResId =  R.mipmap.icon_wx_pay;
        }
        binding.viewItemOnlineIvHeader.setImageResource(typeResId);
        binding.viewItemOnlineTvType.setText(type);
    }
}