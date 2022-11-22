package com.iskyun.im.ui.common;

import android.view.LayoutInflater;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;

import com.gyf.immersionbar.ImmersionBar;
import com.iskyun.im.ImLiveApp;
import com.iskyun.im.R;
import com.iskyun.im.adapter.RechargeAdapter;
import com.iskyun.im.base.BaseActivity;
import com.iskyun.im.common.Constant;
import com.iskyun.im.data.bean.Diamond;
import com.iskyun.im.data.bean.PayInfo;
import com.iskyun.im.data.bean.Recharge;
import com.iskyun.im.data.bean.RechargeType;
import com.iskyun.im.data.bean.User;
import com.iskyun.im.data.req.OrderBody;
import com.iskyun.im.databinding.ActivityRechargeBinding;
import com.iskyun.im.helper.LiveDataBus;
import com.iskyun.im.utils.GsonUtils;
import com.iskyun.im.utils.LogUtils;
import com.iskyun.im.utils.ToastUtils;
import com.iskyun.im.utils.event.DiamondChangeEvent;
import com.iskyun.im.utils.event.PayEvent;
import com.iskyun.im.utils.manager.SpManager;
import com.iskyun.im.ui.common.viewmodel.RechargeViewModel;
import com.tencent.mm.opensdk.modelpay.PayReq;

/**
 * 充值页面
 */
public class RechargeActivity extends BaseActivity<ActivityRechargeBinding, RechargeViewModel> implements View.OnClickListener {

    private RechargeAdapter rechargeAdapter;

    @Override
    public RechargeViewModel onCreateViewModel(ViewModelProvider provider) {
        return provider.get(RechargeViewModel.class);
    }

    @Override
    public ActivityRechargeBinding onCreateViewBinding(LayoutInflater inflater) {
        return ActivityRechargeBinding.inflate(inflater);
    }

    @Override
    protected ToolbarHeader getToolbarHeader() {
        return ToolbarHeader.CustomHeader;
    }

    private Recharge selectedRecharge;

    @Override
    public void initBase() {
        User user = SpManager.getInstance().getCurrentUser();
        if (user == null)
            return;
        ImmersionBar.with(this).titleBar(mViewBinding.tlHeader)
                .keyboardEnable(true)
                .init();
        mViewBinding.tlBack.setOnClickListener(v -> finish());
        mViewBinding.tlTitle.setText(getTitleText());

        mViewBinding.rechargeBtnPay.setOnClickListener(this);
        mViewBinding.rechargeTvAmount.setText(String.valueOf(user.getUserDiamond()));

        initRv(mViewBinding.recyclerView);
        rechargeAdapter = new RechargeAdapter();
        mViewBinding.recyclerView.setAdapter(rechargeAdapter);

        rechargeAdapter.setOnItemClickListener((adapter, view, position) -> setSelectedPosition(position));

        //支付成功通知
        LiveDataBus.get().with(Constant.PAY_SUCCESS, PayEvent.class).observe(this, payEvent -> {
            if (selectedRecharge != null) {
                if (selectedRecharge instanceof Diamond) {
                    int diamond = user.getUserDiamond() + ((Diamond) selectedRecharge).getDiamondNumber();
                    mViewBinding.rechargeTvAmount.setText(String.valueOf(diamond));
                    user.setUserDiamond(diamond);
                }
                SpManager.getInstance().setCurrentUser(user);
                LiveDataBus.get().with(Constant.DIAMOND_CHANGE).postValue(new DiamondChangeEvent());
            }
        });
        mViewModel.observerPayInfo().observe(this, this::pay);

        mViewModel.observeDiamonds().observe(this, diamonds -> rechargeAdapter.setList(diamonds));
    }

    private void setSelectedPosition(int position) {
        rechargeAdapter.selectPosition(position);
        if (position != RechargeAdapter.DEFAULT_SELECT) {
            selectedRecharge = rechargeAdapter.getItem(position);
        }
    }

    @Override
    public int getTitleText() {
        return R.string.recharge_center;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.recharge_btn_pay) {
            if (selectedRecharge == null) {
                ToastUtils.showToast("请选择要购买的商品");
                return;
            }
            OrderBody orderBody = new OrderBody();
            orderBody.setOrderType(RechargeType.Diamond.getType());
            orderBody.setId(selectedRecharge.getId());
            mViewModel.getPayInfo(orderBody);
        }
    }

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

}