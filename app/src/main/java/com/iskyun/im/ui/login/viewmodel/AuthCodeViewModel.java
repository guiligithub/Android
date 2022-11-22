package com.iskyun.im.ui.login.viewmodel;

import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableInt;

import com.iskyun.im.R;
import com.iskyun.im.base.BaseViewModel;
import com.iskyun.im.utils.AuthUtils;
import com.iskyun.im.utils.ToastUtils;

public class AuthCodeViewModel extends BaseViewModel<String> {

    //倒计时状态
    public ObservableBoolean countDownStatus = new ObservableBoolean(false);
    public ObservableInt countObserve = new ObservableInt(TOTAL_COUNT);
    private static final int TOTAL_COUNT = 60;
    private int count = TOTAL_COUNT - 1;
    private final Runnable runnable = new VerifyRunnable();
    private final Handler handler = new Handler();


    @Override
    protected void onCleared() {
        super.onCleared();
        removeCallbacks();
    }


    /**
     * activity 推出时清楚 handler
     */
    public void removeCallbacks() {
        handler.removeCallbacks(runnable);
    }


    @Override
    public void onError(Throwable e) {
        super.onError(e);
        clearVerify(runnable);
        ToastUtils.showToast(R.string.sms_send_fail);
    }

    /**
     * 60s 倒计时
     */
    private class VerifyRunnable implements Runnable {

        @Override
        public void run() {
            count--;
            if (count > 0) {
                countObserve.set(count);
                handler.postDelayed(this, 1000);
            } else {
                clearVerify(this);
            }
        }
    }

    /**
     * 清除倒计时
     *
     * @param runnable
     */
    private void clearVerify(Runnable runnable) {
        countDownStatus.set(false);
        countObserve.set(TOTAL_COUNT);
        handler.removeCallbacks(runnable);
        count = TOTAL_COUNT - 1;
    }

    @Override
    public void onSuccess(String o) {
        ToastUtils.showToast(R.string.sms_send_success);
    }

    public void sendCode(@NonNull String mobile) {
        if (!AuthUtils.verifyNumberPhone(mobile)) {
            return;
        }
        if (countDownStatus.get()) {
            return;
        }
        userRepository.verificationCode(mobile).map(this).subscribe(this);
        handler.postDelayed(runnable, 1000);
        countDownStatus.set(true);
    }
}
