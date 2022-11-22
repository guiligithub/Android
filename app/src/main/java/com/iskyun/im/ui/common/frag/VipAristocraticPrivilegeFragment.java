package com.iskyun.im.ui.common.frag;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;

import com.iskyun.im.R;
import com.iskyun.im.adapter.AristocraticPrivilegeItemAdapter;
import com.iskyun.im.base.BaseFragment;
import com.iskyun.im.data.bean.MineItemModel;
import com.iskyun.im.data.bean.VipType;
import com.iskyun.im.databinding.FragmentVipAristocraticPrivilegesBinding;
import com.iskyun.im.ui.mine.viewmodel.MineModel;

import java.util.ArrayList;
import java.util.List;

public class VipAristocraticPrivilegeFragment extends BaseFragment<FragmentVipAristocraticPrivilegesBinding, MineModel> implements View.OnClickListener {
    public static final String VIP_TYPE = "VIP_TYPE";

    public VipAristocraticPrivilegeFragment(){}

    public static VipAristocraticPrivilegeFragment newInstance(int vipType) {
        VipAristocraticPrivilegeFragment fragment = new VipAristocraticPrivilegeFragment();
        Bundle args = new Bundle();
        args.putInt(VIP_TYPE, vipType);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public MineModel onCreateViewModel(ViewModelProvider provider) {
        return provider.get(MineModel.class);
    }

    @Override
    public FragmentVipAristocraticPrivilegesBinding onCreateViewBinding(LayoutInflater inflater) {
        return FragmentVipAristocraticPrivilegesBinding.inflate(inflater);
    }

    @Override
    public void initBase() {
        AristocraticPrivilegeItemAdapter itemAdapter = new AristocraticPrivilegeItemAdapter(R.layout.view_arstocratic_privileg_item);
        if (getArguments() != null) {
            int vipType = getArguments().getInt(VIP_TYPE, VipType.VIP_OF_C.getType());
            if(vipType == VipType.VIP_OF_C.getType()){
                itemAdapter.setList(getVipModels());
            }else {
                itemAdapter.setList(getSVipModels());
            }
        }
        mViewBinding.nobleRightsAndInterestsRecyclerview.setAdapter(itemAdapter);
    }

    /**
     * item
     */
    private List<MineItemModel> getVipModels() {
        List<MineItemModel> items = getCommonModels();
        items.add(new MineItemModel(getString(R.string.noble_nameplate),
                R.mipmap.icon_noble_nameplate_1, ""));
        items.add(new MineItemModel(getString(R.string.call_discount),
                R.mipmap.icon_call_discount_1, ""));
        items.add(new MineItemModel(getString(R.string.create_group_chat),
                R.mipmap.icon_create_group_chat_1, ""));
        items.add(new MineItemModel(getString(R.string.vip_seats),
                R.mipmap.icon_vip_seats_1, ""));
        return items;
    }

    private List<MineItemModel> getSVipModels() {
        List<MineItemModel> items = getCommonModels();
        items.add(new MineItemModel(getString(R.string.noble_nameplate),
                R.mipmap.icon_noble_nameplate, ""));
        items.add(new MineItemModel(getString(R.string.call_discount),
                R.mipmap.icon_call_discount, ""));
        items.add(new MineItemModel(getString(R.string.create_group_chat),
                R.mipmap.icon_create_group_chat, ""));
        items.add(new MineItemModel(getString(R.string.vip_seats),
                R.mipmap.icon_vip_seats, ""));
        return items;
    }

    private List<MineItemModel> getCommonModels(){
        List<MineItemModel> items = new ArrayList<>();
        items.add(new MineItemModel(getString(R.string.aristocratic_privilege),
                R.mipmap.icon_aristocratic_privilege, ""));
        items.add(new MineItemModel(getString(R.string.noble_gifts),
                R.mipmap.icon_noble_gifts, ""));
        items.add(new MineItemModel(getString(R.string.stealth_privilege),
                R.mipmap.icon_stealth_privilege, ""));
        items.add(new MineItemModel(getString(R.string.recharge_discount),
                R.mipmap.icon_recharge_discount, ""));
        items.add(new MineItemModel(getString(R.string.noble_bullet_screen),
                R.mipmap.icon_noble_discount, ""));
        items.add(new MineItemModel(getString(R.string.expression_free),
                R.mipmap.icon_expression_free, ""));
        items.add(new MineItemModel(getString(R.string.front_row_wheat_preface),
                R.mipmap.icon_front_row_wheat_preface, ""));
        items.add(new MineItemModel(getString(R.string.gift_broadcast),
                R.mipmap.icon_gift_broadcast, ""));
        return items;
    }

    @Override
    public int getTitleText() {
        return 0;
    }

    @Override
    public void onClick(View v) {

    }
}
