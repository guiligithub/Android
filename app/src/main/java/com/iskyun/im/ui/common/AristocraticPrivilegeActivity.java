package com.iskyun.im.ui.common;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.gyf.immersionbar.ImmersionBar;
import com.iskyun.im.ImLiveApp;
import com.iskyun.im.R;
import com.iskyun.im.base.BaseActivity;
import com.iskyun.im.data.bean.Vip;
import com.iskyun.im.data.bean.VipType;
import com.iskyun.im.databinding.ActivityAristocraticPrivilegeBinding;
import com.iskyun.im.ui.common.frag.VipAristocraticPrivilegeFragment;
import com.iskyun.im.utils.ActivityUtils;
import com.iskyun.im.utils.TabUtils;
import com.iskyun.im.ui.common.viewmodel.RechargeViewModel;
import com.iskyun.im.widget.SlidingTabLayout;

import java.util.ArrayList;
import java.util.List;

public class AristocraticPrivilegeActivity extends BaseActivity<ActivityAristocraticPrivilegeBinding, RechargeViewModel> implements View.OnClickListener {
    public static final String VIP_TYPE = "VIP_TYPE";
    private List<Fragment> fragments = new ArrayList<>();
    private double firstVipPrice;
    private double firstSVipPrice;

    public static void launcher(int vipType) {
        Bundle bundle = new Bundle();
        bundle.putInt(VIP_TYPE, vipType);
        ActivityUtils.launcher(ImLiveApp.get().getTopActivity(), bundle, AristocraticPrivilegeActivity.class);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tl_back) {
            finish();
        }else if(view.getId() == R.id.aristocratic_tv_recharge){
            if(mViewBinding.aristocraticTabs.getCurrentTab() ==0){
                VipRechargeActivity.launcher(VipType.VIP_OF_C.getType());
            }else {
                VipRechargeActivity.launcher(VipType.VIP_OF_S.getType());
            }
            finish();
        }
    }

    @Override
    public RechargeViewModel onCreateViewModel(ViewModelProvider provider) {
        return provider.get(RechargeViewModel.class);
    }

    @Override
    public ActivityAristocraticPrivilegeBinding onCreateViewBinding(LayoutInflater inflater) {
        return ActivityAristocraticPrivilegeBinding.inflate(inflater);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImmersionBar.with(this).statusBarDarkFont(false).titleBar(mViewBinding.getRoot().findViewById(R.id.tl_header))
                .init();
    }

    @Override
    public void initBase() {
        mViewBinding.tlBack.setOnClickListener(this::onClick);
        mViewBinding.aristocraticTvRecharge.setOnClickListener(this::onClick);
        initFragment();

        mViewModel.observeVips().observe(this, vips -> {
            firstVipPrice = getFirstPrice(vips, VipType.VIP_OF_C.getType());
            firstSVipPrice = getFirstPrice(vips, VipType.VIP_OF_S.getType());
            if(mViewBinding.aristocraticTabs.getCurrentTab() ==0){
                mViewBinding.aristocraticTvPrice.setText(String.valueOf(firstVipPrice));
            }else {
                mViewBinding.aristocraticTvPrice.setText(String.valueOf(firstSVipPrice));
            }
        });
    }

    private void initFragment() {
        VipAristocraticPrivilegeFragment vipFragment =  VipAristocraticPrivilegeFragment
                .newInstance(VipType.VIP_OF_C.getType());
        VipAristocraticPrivilegeFragment sVipFragment = VipAristocraticPrivilegeFragment
                .newInstance(VipType.VIP_OF_S.getType());
        fragments.add(vipFragment);
        fragments.add(sVipFragment);
        TabUtils.init(this, mViewBinding.aristocraticVPager, mViewBinding.aristocraticTabs,
                fragments, new String[]{getString(R.string.vip),
                        getString(R.string.svip)});
        mViewBinding.aristocraticTabs.setOnTabSelectListener(new SlidingTabLayout.OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                if (position == 0) {
                    mViewBinding.aristocraticIvVipType.setImageResource(R.mipmap.icon_aristocratic_privilege_vip_bg);
                    mViewBinding.aristocraticTvPrice.setText(String.valueOf(firstVipPrice));
                } else {
                    mViewBinding.aristocraticIvVipType.setImageResource(R.mipmap.icon_aristocratic_privilege_svip_bg);
                    mViewBinding.aristocraticTvPrice.setText(String.valueOf(firstSVipPrice));
                }
            }

            @Override
            public void onTabReselect(int position) {
            }
        });
    }

    @Override
    public int getTitleText() {
        return 0;
    }

    @Override
    protected ToolbarHeader getToolbarHeader() {
        return ToolbarHeader.CustomHeader;
    }

    private double getFirstPrice(List<Vip> vips, int vipType) {
        double vipPrice = 0d;
        for (Vip vip : vips) {
            if (vip.getVipType() == vipType) {
                vipPrice = vip.getVipPrice();
                break;
            }
        }
        return vipPrice;
    }
}
