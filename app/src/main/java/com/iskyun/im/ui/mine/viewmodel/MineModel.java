package com.iskyun.im.ui.mine.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.iskyun.im.base.BaseViewModel;
import com.iskyun.im.data.bean.Balance;
import com.iskyun.im.data.bean.Relation;

public class MineModel extends BaseViewModel<Relation> {

    private final MutableLiveData<Relation> relationLiveData = new MutableLiveData<>();
    private final MutableLiveData<Balance> balanceLiveData = new MutableLiveData<>();

    @Override
    public void onSuccess(Relation relation) {
        relationLiveData.postValue(relation);
    }

    /**
     * 关注数
     * @return
     */
    public LiveData<Relation> refreshMyAttention() {
        setShowDialog(false);
        userRepository.refreshMyAttention().map(this).subscribe(this);
        return relationLiveData;
    }

    /**
     * 查询余额
     */
    public LiveData<Balance> selectBalance() {
        setShowDialog(false);
        userRepository.selectBalance().map(new HttpResultFunc<>())
                .subscribe(this::onBalanceNext, this::onError, this::onComplete);
        return balanceLiveData;
    }

    private void onBalanceNext(Balance balance) {
        balanceLiveData.setValue(balance);
    }
}