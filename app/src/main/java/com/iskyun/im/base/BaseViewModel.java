package com.iskyun.im.base;

import androidx.lifecycle.ViewModel;

import com.iskyun.im.ImLiveApp;
import com.iskyun.im.data.UserRepository;
import com.iskyun.im.data.net.AppException;
import com.iskyun.im.data.resp.AppResponse;
import com.iskyun.im.ui.dialog.AppLoadingDialog;
import com.iskyun.im.utils.ActivityUtils;
import com.iskyun.im.utils.LogUtils;
import com.iskyun.im.utils.ToastUtils;
import com.iskyun.im.utils.manager.ExecutorManager;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Function;

public abstract class BaseViewModel<T> extends ViewModel implements Function<AppResponse<T>, T>, Observer<T> {

    protected UserRepository userRepository;
    protected boolean showDialog = true;

    protected final AppLoadingDialog loadingDialog;

    public BaseViewModel() {
        userRepository = UserRepository.get();
        loadingDialog = AppLoadingDialog.get(ImLiveApp.get().getTopActivity());
    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {
        LogUtils.e("onSubscribe");
        showDialog();
    }

    @Override
    public void onNext(@NonNull T t) {
        LogUtils.e("onNext");
        onSuccess(t);
    }

    @Override
    public void onError(@NonNull Throwable e) {
        LogUtils.e("onError"+e.getMessage());
        if (e instanceof AppException) {
            AppException ex = (AppException) e;
            ExecutorManager.getInstance().runOnMainThread(() -> ToastUtils.showToast(ex.getMessage()));
        }

        dismissDialog();
    }

    @Override
    public void onComplete() {
        LogUtils.e("onComplete");
        dismissDialog();
    }

    /**
     * 服务器返回code 处理
     *
     * @param response
     * @return
     * @throws Throwable
     */
    @Override
    public T apply(AppResponse<T> response) throws Throwable {
        LogUtils.e("--apply--");
        if (response == null)
            throw new Exception();
        if (response.getCode() < 0) {
            throw new AppException(response.getMsg(), response.getCode());
        } else if (response.getCode() == 500) {
            throw new AppException(response.getMsg(), response.getCode());
        } else if (response.getCode() == 666) {
            //token 失效
            ActivityUtils.tokenInvalid();
            throw new AppException(response.getMsg(), response.getCode());
        } else if (response.getCode() == 200) {
        }
        return response.getData();
    }

    public abstract void onSuccess(T t);

    public static class HttpResultFunc<T> implements Function<AppResponse<T>, T> {

        @Override
        public T apply(AppResponse<T> response) throws Throwable {
            if (response == null)
                throw new Exception();
            if (response.getCode() < 0) {
                throw new AppException(response.getMsg(), response.getCode());
            } else if (response.getCode() == 500) {
                throw new AppException(response.getMsg(), response.getCode());
            } else if (response.getCode() == 666) {
                //token 失效
                ActivityUtils.tokenInvalid();
                throw new AppException(response.getMsg(), response.getCode());
            } else if (response.getCode() == 200) {
                return response.getData();
            }
            throw new AppException(response.getMsg(), response.getCode());
        }
    }

    public static class HttpResultFuncStr implements Function<AppResponse<String>, String> {

        @Override
        public String apply(AppResponse<String> response) throws Throwable {
            LogUtils.e("HttpResultFuncStr");
            if (response == null)
                throw new Exception();
            if (response.getCode() < 0) {
                throw new AppException(response.getMsg(), response.getCode());
            } else if (response.getCode() == 500) {
                throw new AppException(response.getMsg(), response.getCode());
            } else if (response.getCode() == 666) {
                //token 失效
                ActivityUtils.tokenInvalid();
                throw new AppException(response.getMsg(), response.getCode());
            } else if (response.getCode() == 200) {
                if (response.getData() == null) {
                    return response.getMsg();
                }
                return response.getData();
            }
            throw new AppException(response.getMsg(), response.getCode());
        }
    }

    public void showDialog(){
        if (loadingDialog != null && showDialog && !loadingDialog.isShowing()) {
            ExecutorManager.getInstance().runOnMainThread(loadingDialog::show);
        }
    }

    public void dismissDialog(){
        if (loadingDialog != null && showDialog && loadingDialog.isShowing()) {
            ExecutorManager.getInstance().runOnMainThread(loadingDialog::dismiss);
        }
    }

    public void setShowDialog(boolean showDialog) {
        this.showDialog = showDialog;
    }
}
