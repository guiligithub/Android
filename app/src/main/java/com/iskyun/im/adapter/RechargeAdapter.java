package com.iskyun.im.adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder;
import com.iskyun.im.R;
import com.iskyun.im.data.bean.Diamond;
import com.iskyun.im.data.bean.Recharge;
import com.iskyun.im.data.bean.Vip;
import com.iskyun.im.data.bean.VipType;
import com.iskyun.im.databinding.ViewItemRechargeBinding;
import com.iskyun.im.utils.DisplayUtils;

public class RechargeAdapter extends BaseQuickAdapter<Recharge, BaseDataBindingHolder<ViewItemRechargeBinding>> {
    private static final int ITEM_TYPE_C_VIP = 0x11;
    private static final int ITEM_TYPE_S_VIP = 0x12;
    private static final int ITEM_TYPE_DIAMOND = 0x13;
    public static final int DEFAULT_SELECT = -1;
    private int selectIndex = DEFAULT_SELECT;

    public RechargeAdapter() {
        super(R.layout.view_item_recharge);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseDataBindingHolder<ViewItemRechargeBinding> holder, int position) {
        super.onBindViewHolder(holder, position);
        ViewItemRechargeBinding binding = holder.getDataBinding();
        if (binding == null)
            return;
        Recharge recharge = getItem(position);
        int itemType = getItemViewType(position);
        if(itemType == ITEM_TYPE_DIAMOND){
            binding.itemRechargeGroup.setVisibility(View.VISIBLE);
            binding.itemVipRechargeGroup.setVisibility(View.GONE);
            binding.itemSVipRechargeGroup.setVisibility(View.GONE);

            binding.itemRechargeTvAmount.setCompoundDrawablesWithIntrinsicBounds(
                    ContextCompat.getDrawable(getContext(), R.mipmap.icon_item_recharge_diamond), null, null, null);
            binding.itemRechargeTvAmount.setText(String.valueOf(((Diamond) recharge).getDiamondNumber()));
            if (((Diamond) recharge).getGiveNumber() != 0) {
                binding.itemRechargeTvGiveAmount.setVisibility(View.VISIBLE);
                String giveContent = getContext().getString(R.string.give_away)
                        + ((Diamond) recharge).getGiveNumber();
                binding.itemRechargeTvGiveAmount.setText(giveContent);
            } else {
                binding.itemRechargeTvGiveAmount.setText("");
                binding.itemRechargeTvGiveAmount.setVisibility(View.INVISIBLE);
            }
            String price = "￥" + ((Diamond) recharge).getDiamondPrice();
            binding.itemRechargeTvPrice.setText(price);
        }else {
            binding.itemRechargeGroup.setVisibility(View.GONE);
            String vipPrice = "￥" + ((Vip) recharge).getVipPrice();
            String time = formatVipTime(((Vip) recharge).getVipTime());
            if(itemType == ITEM_TYPE_C_VIP){
                binding.itemSVipRechargeGroup.setVisibility(View.GONE);
                binding.itemVipRechargeGroup.setVisibility(View.VISIBLE);
                binding.itemVipRechargeTvVipPrice.setText(vipPrice);
                binding.itemVipRechargeTvVipName.setText(time);
            }else {
                binding.itemSVipRechargeGroup.setVisibility(View.VISIBLE);
                binding.itemVipRechargeGroup.setVisibility(View.GONE);
                binding.itemSVipRechargeTvVipPrice.setText(vipPrice);
                binding.itemSVipRechargeTvVipName.setText(time);
            }
        }
        binding.viewItemRecharge.setSelected(position == selectIndex);
    }

    public void selectPosition(int position) {
        selectIndex = position;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        Recharge recharge = getItem(position);
        if (recharge instanceof Vip) {
            Vip vip = (Vip) recharge;
            if(vip.getVipType() ==  VipType.VIP_OF_C.getType()){
                return ITEM_TYPE_C_VIP;
            }else if(vip.getVipType() ==  VipType.VIP_OF_S.getType()){
                return ITEM_TYPE_S_VIP;
            }
        }else {
            return ITEM_TYPE_DIAMOND;
        }
        return super.getItemViewType(position);
    }

    public Recharge getSelected() {
        if (selectIndex != -1) {
            return getItem(selectIndex);
        }
        return null;
    }

    @Override
    protected void convert(@NonNull BaseDataBindingHolder<ViewItemRechargeBinding> viewItemRechargeBindingBaseDataBindingHolder, Recharge recharge) {

    }

    private String formatVipTime(int vipTime) {
        String content;
        String day = DisplayUtils.getString(R.string.day);
        String month = DisplayUtils.getString(R.string.month);
        if (vipTime < 30) {
            content = vipTime + day;
        } else {
            content = vipTime / 30 + month;
        }
        return content;
    }

}
