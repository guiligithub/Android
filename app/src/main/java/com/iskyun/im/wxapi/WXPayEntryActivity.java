package com.iskyun.im.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.iskyun.im.ImLiveApp;
import com.iskyun.im.R;
import com.iskyun.im.common.Constant;
import com.iskyun.im.helper.LiveDataBus;
import com.iskyun.im.utils.ToastUtils;
import com.iskyun.im.utils.event.PayEvent;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelpay.PayResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

public class WXPayEntryActivity extends AppCompatActivity implements IWXAPIEventHandler {

    private IWXAPI api;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = ImLiveApp.get().getWXApi();
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {

    }

    @Override
    public void onResp(BaseResp resp) {
        if(resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX){
            int resId;
            if(resp.errCode == 0){
                resId = R.string.pay_success;
                PayResp payResp = (PayResp) resp;
                LiveDataBus.get().with(Constant.PAY_SUCCESS).postValue(new PayEvent());
            }else if(resp.errCode == -2){
                resId = R.string.pay_cancel;
            }else {
                resId = R.string.pay_fail;
            }
            ToastUtils.showToast(resId);
            new Handler(getMainLooper()).postDelayed(this::finish,16);
        }
    }
}
