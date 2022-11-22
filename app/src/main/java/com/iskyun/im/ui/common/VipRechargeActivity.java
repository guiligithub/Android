package com.iskyun.im.ui.common;

import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gyf.immersionbar.ImmersionBar;
import com.iskyun.im.ImLiveApp;
import com.iskyun.im.R;
import com.iskyun.im.adapter.RechargeAdapter;
import com.iskyun.im.adapter.RectangleIndicatorAdapter;
import com.iskyun.im.adapter.VipRightAdapter;
import com.iskyun.im.base.BaseActivity;
import com.iskyun.im.common.Constant;
import com.iskyun.im.data.bean.PayInfo;
import com.iskyun.im.data.bean.Recharge;
import com.iskyun.im.data.bean.RechargeType;
import com.iskyun.im.data.bean.User;
import com.iskyun.im.data.bean.Vip;
import com.iskyun.im.data.bean.VipType;
import com.iskyun.im.data.req.OrderBody;
import com.iskyun.im.databinding.ActivityVipRechargeBinding;
import com.iskyun.im.helper.LiveDataBus;
import com.iskyun.im.utils.ActivityUtils;
import com.iskyun.im.utils.DateUtils;
import com.iskyun.im.utils.DisplayUtils;
import com.iskyun.im.utils.GsonUtils;
import com.iskyun.im.utils.LogUtils;
import com.iskyun.im.utils.ToastUtils;
import com.iskyun.im.utils.UserUtils;
import com.iskyun.im.utils.event.PayEvent;
import com.iskyun.im.utils.manager.SpManager;
import com.iskyun.im.ui.common.viewmodel.RechargeViewModel;
import com.iskyun.im.widget.GridItemDecoration;
import com.tencent.mm.opensdk.modelpay.PayReq;

import java.util.ArrayList;
import java.util.List;

public class VipRechargeActivity extends BaseActivity<ActivityVipRechargeBinding, RechargeViewModel> implements View.OnClickListener {

    public static final String VIP_TYPE = "VIP_TYPE";

    private RechargeAdapter rechargeAdapter;
    private VipRightAdapter vipRightAdapter;
    private int vipType = VipType.VIP_OF_C.getType();
    private RectangleIndicatorAdapter indicatorAdapter;
    private Recharge selectedRecharge;

    public static void launcher(int vipType) {
        Bundle bundle = new Bundle();
        bundle.putInt(VIP_TYPE, vipType);
        ActivityUtils.launcher(ImLiveApp.get().getTopActivity(), bundle, VipRechargeActivity.class);
    }

    @Override
    public RechargeViewModel onCreateViewModel(ViewModelProvider provider) {
        return provider.get(RechargeViewModel.class);
    }

    @Override
    public ActivityVipRechargeBinding onCreateViewBinding(LayoutInflater inflater) {
        return ActivityVipRechargeBinding.inflate(inflater);
    }

    @Override
    protected ToolbarHeader getToolbarHeader() {
        return ToolbarHeader.CustomHeader;
    }

    @Override
    public void initBase() {
        ImmersionBar.with(this).keyboardEnable(true).statusBarDarkFont(false).titleBar(findViewById(R.id.tl_header)).init();
        User user = SpManager.getInstance().getCurrentUser();
        if (user == null)
            return;
        mViewBinding.vipRechargeBtnPay.setOnClickListener(this);
        mViewBinding.tlBack.setOnClickListener(v -> finish());
        mViewBinding.tlTitle.setText(getTitleText());

        if (getIntent() != null && getIntent().getExtras() != null)
            vipType = getIntent().getExtras().getInt(VIP_TYPE, VipType.VIP_OF_C.getType());

        if (vipType == VipType.VIP_OF_S.getType()) {
            mViewBinding.vipRechargeClLayout.setContentScrimColor(getResources().getColor(R.color.s_vip_header_bg_color));
            mViewBinding.vipRechargeTopImage.setBackgroundResource(R.mipmap.icon_s_vip_top);
            mViewBinding.vipRechargeIvHeaderImage.setImageDrawable(null);
            mViewBinding.getRoot().setBackgroundColor(getResources().getColor(R.color.s_vip_bg_color));
            mViewBinding.vipRechargeAppBar.setBackgroundColor(getResources().getColor(R.color.s_vip_bg_color));
            mViewBinding.vipRechargeRvVip.setBackgroundResource(R.drawable.svip_black_bg);
            mViewBinding.tlTitle.setText(getString(R.string.s_vip));
        }

        initVip();
        initVipRight();
        initIndicator();

        //支付成功通知
        LiveDataBus.get().with(Constant.PAY_SUCCESS, PayEvent.class).observe(this, new Observer<PayEvent>() {
            @Override
            public void onChanged(PayEvent payEvent) {
                if (selectedRecharge != null) {
                    Vip vip = ((Vip) selectedRecharge);
                    //续费
                    if (user.getVipType() == vip.getVipType()) {
                        user.setVipExpireTime(DateUtils.getTimeByDay(user.getVipExpireTime(), vip.getVipTime()));
                    } else {
                        user.setVipStartTime(DateUtils.getCurrentTime());
                        user.setVipExpireTime(DateUtils.getTimeByDay(vip.getVipTime()));
                        user.setVip(1);
                        user.setVipType(vip.getVipType());
                    }
                    SpManager.getInstance().setCurrentUser(user);
                    setUserInfo();
                    LiveDataBus.get().with(Constant.VIP_CHANGE).postValue(new PayEvent());
                    finish();
                }
            }
        });
        mViewModel.observerPayInfo().observe(this, this::pay);

        mViewModel.observeVips().observe(this, diamonds -> {
            List<Vip> cVips = new ArrayList<>();
            for (Vip vip : diamonds) {
                if (vip.getVipType() == vipType) {
                    cVips.add(vip);
                }
            }
            rechargeAdapter.setList(cVips);
        });

        setUserInfo();
    }

    @Override
    public int getTitleText() {
        return R.string.vip;
    }

    private void initVip() {
        initRv(mViewBinding.vipRechargeRvVip);
        if (vipType == VipType.VIP_OF_S.getType()) {
            GridItemDecoration decoration = new GridItemDecoration(this,
                    getResources().getDimensionPixelSize(R.dimen.grid_decoration_width), R.color.s_vip_item_bg_color);
            mViewBinding.vipRechargeRvVip.addItemDecoration(decoration);
        }
        rechargeAdapter = new RechargeAdapter();
        mViewBinding.vipRechargeRvVip.setAdapter(rechargeAdapter);
        rechargeAdapter.setOnItemClickListener((adapter, view, position) -> {
            rechargeAdapter.selectPosition(position);
            selectedRecharge = rechargeAdapter.getItem(position);
        });
    }

    private void initVipRight() {
        LinearLayoutManager lm = (LinearLayoutManager) mViewBinding.vipRechargeRvRight.getLayoutManager();
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this,
                DividerItemDecoration.HORIZONTAL);
        itemDecoration.setDrawable(DisplayUtils.getDrawable(R.drawable.divider_h_16dp));
        mViewBinding.vipRechargeRvRight.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mViewBinding.vipRechargeRvRight.addItemDecoration(itemDecoration);

        vipRightAdapter = new VipRightAdapter();
        mViewBinding.vipRechargeRvRight.setAdapter(vipRightAdapter);
        if (vipType == VipType.VIP_OF_C.getType()) {
            vipRightAdapter.setList(getVipRights());
        } else {
            vipRightAdapter.setList(getSVipRights());
        }

        mViewBinding.vipRechargeRvRight.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lm != null) {
                    int position = lm.findLastCompletelyVisibleItemPosition();
                    indicatorAdapter.setSelectedPosition(position / 3);
                }
            }
        });

    }

    /**
     * 导航
     */
    private void initIndicator() {
        indicatorAdapter = new RectangleIndicatorAdapter();
        mViewBinding.vipRechargeRvIndicator.setAdapter(indicatorAdapter);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this,
                DividerItemDecoration.HORIZONTAL);
        itemDecoration.setDrawable(DisplayUtils.getDrawable(R.drawable.divider_indicator));

        indicatorAdapter.setPageCount((int) Math.ceil(vipRightAdapter.getItemCount()
                * 1.0f / 3));
        mViewBinding.vipRechargeRvIndicator.setNestedScrollingEnabled(true);
        mViewBinding.vipRechargeRvIndicator.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mViewBinding.vipRechargeRvIndicator.addItemDecoration(itemDecoration);
        indicatorAdapter.setSelectedPosition(0);
    }

    /**
     * vip权益
     *
     * @return
     */
    private List<VipRightAdapter.VipRightModel> getVipRights() {
        List<VipRightAdapter.VipRightModel> vipRights = new ArrayList<>();
        String[] vipRightTitle = getResources().getStringArray(R.array.vip_right_title);
        String[] vipRightContent = getResources().getStringArray(R.array.vip_right_content);
        String[] vipRightTitleColors = getResources().getStringArray(R.array.vip_right_title_color);
        TypedArray typedArray = getResources().obtainTypedArray(R.array.vip_right_image);
        VipRightAdapter.VipRightModel vipRightModel;
        for (int i = 0; i < vipRightTitle.length; i++) {
            vipRightModel = new VipRightAdapter.VipRightModel();
            vipRightModel.title = vipRightTitle[i];
            vipRightModel.content = vipRightContent[i];
            vipRightModel.image = typedArray.getResourceId(i, -1);
            vipRightModel.titleColor = Color.parseColor(vipRightTitleColors[i]);
            vipRights.add(vipRightModel);
        }
        typedArray.recycle();
        return vipRights;
    }

    /**
     * svip权益
     *
     * @return
     */
    private List<VipRightAdapter.VipRightModel> getSVipRights() {
        List<VipRightAdapter.VipRightModel> vipRights = new ArrayList<>();
        String[] vipRightTitle = getResources().getStringArray(R.array.s_vip_right_title);
        String[] vipRightContent = getResources().getStringArray(R.array.s_vip_right_content);
        String[] vipRightTitleColors = getResources().getStringArray(R.array.s_vip_right_title_color);
        TypedArray typedArray = getResources().obtainTypedArray(R.array.s_vip_right_image);
        VipRightAdapter.VipRightModel vipRightModel;
        for (int i = 0; i < vipRightTitle.length; i++) {
            vipRightModel = new VipRightAdapter.VipRightModel();
            vipRightModel.title = vipRightTitle[i];
            vipRightModel.content = vipRightContent[i];
            vipRightModel.image = typedArray.getResourceId(i, -1);
            vipRightModel.titleColor = Color.parseColor(vipRightTitleColors[i]);
            vipRights.add(vipRightModel);
        }
        LogUtils.e(GsonUtils.toJson(vipRights));
        typedArray.recycle();
        return vipRights;
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.vip_recharge_btn_pay) {
            if (selectedRecharge == null) {
                ToastUtils.showToast("请选择要购买的商品");
                return;
            }

            OrderBody orderBody = new OrderBody();
            orderBody.setOrderType(RechargeType.Vip.getType());
            orderBody.setId(selectedRecharge.getId());
            mViewModel.getPayInfo(orderBody);
        }
    }

    /**
     * 发起支付
     *
     * @param payInfo
     */
    private void pay(PayInfo payInfo) {
        if (payInfo == null)
            return;
        PayReq payReq = new PayReq();
        payReq.appId = payInfo.getAppId();
        payReq.partnerId = payInfo.getPartnerId();
        payReq.prepayId = payInfo.getPrepayId();
        payReq.packageValue = payInfo.getPackageValue();
        payReq.nonceStr = payInfo.getNonceStr();
        payReq.timeStamp = payInfo.getTimeStamp();
        payReq.sign = payInfo.getPaySign();
        payReq.signType = payInfo.getSignType();
        LogUtils.e("" + GsonUtils.toJson(payReq));
        ImLiveApp.get().getWXApi().sendReq(payReq);
    }

    private void setUserInfo() {
        UserUtils.setHeaderUrl(mViewBinding.vipRechargeIvAvatar);
        UserUtils.setNickname(mViewBinding.vipRechargeTvName);
        String status;
        int vipIcon;
        User user = SpManager.getInstance().getCurrentUser();

        if (vipType == VipType.VIP_OF_S.getType()) {
            status = getString(R.string.no_open_svip);
            vipIcon = R.mipmap.icon_svip_no_crown;
            if (user.getVipType() == VipType.VIP_OF_S.getType()) {
                vipIcon = R.mipmap.icon_svip_an_crown;
                status = getString(R.string.opened_svip);
            } else if (user.getVipType() == VipType.VIP_OF_C.getType()) {
                vipIcon = R.mipmap.icon_vip_an_crown;
                status = getString(R.string.opened_vip);
            }
        } else if (vipType == VipType.VIP_OF_C.getType()) {
            status = getString(R.string.no_open_vip);
            vipIcon = R.mipmap.icon_vip_no_crown;
            if (user.getVipType() == VipType.VIP_OF_C.getType()) {
                vipIcon = R.mipmap.icon_vip_an_crown;
                status = getString(R.string.opened_vip);
            } else if (user.getVipType() == VipType.VIP_OF_S.getType()) {
                vipIcon = R.mipmap.icon_svip_an_crown;
                status = getString(R.string.opened_vip);
            }
        } else {
            status = getString(R.string.no_open_vip);
            vipIcon = R.mipmap.icon_vip_no_crown;
        }
        mViewBinding.vipRechargeTvStatus.setText(status);
        mViewBinding.vipRechargeIvVipIcon.setImageResource(vipIcon);
    }
}