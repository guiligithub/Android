package com.iskyun.im.ui.common.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.iskyun.im.base.BaseViewModel;
import com.iskyun.im.data.bean.Diamond;
import com.iskyun.im.data.bean.PayInfo;
import com.iskyun.im.data.bean.Vip;
import com.iskyun.im.data.net.AppException;
import com.iskyun.im.data.net.RetrofitManager;
import com.iskyun.im.data.req.OrderBody;
import com.iskyun.im.data.resp.AppResponse;
import com.iskyun.im.data.resp.ListRecords;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RechargeViewModel extends BaseViewModel<ListRecords<Diamond>> {

    private final MutableLiveData<List<Diamond>> diamondLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Vip>> vipLiveData = new MutableLiveData<>();
    private final MutableLiveData<PayInfo> payInfoLiveData = new MutableLiveData<>();


    @Override
    public void onSuccess(ListRecords<Diamond> diamonds) {
        if(diamonds == null)
            return;
        diamondLiveData.postValue(diamonds.getRecords());

    }

    /**
     *
     */
    private void getRechargeDiamonds(){
        userRepository.getRechargeDiamonds().map(this).subscribe(this);
    }

    public LiveData<List<Diamond>> observeDiamonds(){
       getRechargeDiamonds();
       return diamondLiveData;
    }

    private void getRechargeVips(){
        userRepository.getRechargeVips().map(new HttpResultFunc<>())
                .subscribe(this::vips,this::onError,this::onComplete);
    }


    public LiveData<List<Vip>> observeVips(){
        getRechargeVips();
        return vipLiveData;
    }

    private void vips(ListRecords<Vip> vipListRecords){
        vipLiveData.postValue(vipListRecords.getRecords());
    }

    public void getPayInfo(OrderBody orderBody){
        if(loadingDialog != null && !loadingDialog.isShowing()){
            loadingDialog.show();
        }
        RetrofitManager.get().getApiUserService().getOrder(orderBody)
                .flatMap((Function<AppResponse<String>, ObservableSource<AppResponse<PayInfo>>>) response -> {
                    if(response != null && response.getCode() == 200){
                        return RetrofitManager.get().getApiUserService().wxPay(response.getData());
                    }
                    return null;
                })
                .map(response -> {
                    if(response.getCode() == 200){
                        return response.getData();
                    }
                    throw new AppException(response.getMsg(),response.getCode());
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onPayNext,this::onError,this::onComplete);

    }

    public LiveData<PayInfo> observerPayInfo(){
        return payInfoLiveData;
    }

    private void onPayNext(PayInfo payInfo) {
        payInfoLiveData.postValue(payInfo);
    }


}
