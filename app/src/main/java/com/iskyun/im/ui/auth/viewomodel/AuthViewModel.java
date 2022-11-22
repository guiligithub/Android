package com.iskyun.im.ui.auth.viewomodel;

import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.iskyun.im.R;
import com.iskyun.im.base.BaseViewModel;
import com.iskyun.im.data.bean.AnchorAuth;
import com.iskyun.im.utils.ToastUtils;

public class AuthViewModel extends BaseViewModel<AnchorAuth> {

    private final MutableLiveData<AnchorAuth> anchorAuthDetailLd = new MutableLiveData<>();
    private final MutableLiveData<String> anchorAuthLd = new MutableLiveData<>();

    public AuthViewModel() {
    }

    @Override
    public void onSuccess(AnchorAuth anchorAuth) {
        anchorAuthDetailLd.postValue(anchorAuth);
    }


    public void anchorAuth(AnchorAuth anchorAuth) {
        if (anchorAuth.getLaborCode() == 0
                || TextUtils.isEmpty(anchorAuth.getVideoAuthUrl())
                || TextUtils.isEmpty(anchorAuth.getIdCardFaceUrl())
                || TextUtils.isEmpty(anchorAuth.getIdCardBackUrl())) {
            ToastUtils.showToast(R.string.complete_auth_information);
            return;
        }
        userRepository.anchorAuth(anchorAuth).map(new HttpResultFuncStr())
                .subscribe(this::anchorAuthOnNext, this::onError, this::onComplete);
    }

    private void anchorAuthOnNext(String s) {
        anchorAuthLd.postValue(s);
    }

    public LiveData<String> observerAnchorAuth() {
        return anchorAuthLd;
    }

    public LiveData<AnchorAuth> getMyAuthDetails() {
        userRepository.getMyAuthDetails().map(this).subscribe(this);

        return anchorAuthDetailLd;
    }

}
