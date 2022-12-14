package com.iskyun.im.data.net.impl;

import androidx.annotation.Nullable;

public abstract class OnResourceParseCallback<T> {
    public boolean hideErrorMsg;

    public OnResourceParseCallback() {}

    /**
     * 是否展示错误信息
     * @param hideErrorMsg
     */
    public OnResourceParseCallback(boolean hideErrorMsg) {
        this.hideErrorMsg = hideErrorMsg;
    }
    /**
     * 成功
     * @param data
     */
    public abstract void onSuccess(@Nullable T data);

    /**
     * 失败
     * @param code
     * @param message
     */
    public void onError(int code, String message){}

    /**
     * 加载中
     */
    public void onLoading(@Nullable T data){}

    /**
     * 隐藏加载
     */
    public void hideLoading(){}
}
