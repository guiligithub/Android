package com.iskyun.im.utils;

import androidx.annotation.NonNull;

import java.util.regex.Pattern;

public class AuthUtils {

    /**
     * 短信验证码验证
     * @param authCode
     * @return
     */
    public static boolean verifyAuthCode(@NonNull String authCode) {
        Pattern pattern = Pattern.compile(RegexConstant.AUTH_CODE.regex);
        boolean result = pattern.matcher(authCode).matches();
        if(!result){
            ToastUtils.showToast(RegexConstant.AUTH_CODE.text);
        }
        return result;
    }

    /**
     * 密码验证
     * @param password
     * @return
     */
    public static boolean verifyPassword(@NonNull String password) {
        Pattern pattern = Pattern.compile(RegexConstant.PASSWORD.regex);
        boolean result = pattern.matcher(password).matches();
        if(!result){
            ToastUtils.showToast(RegexConstant.PASSWORD.text);
        }
        return result;
    }

    /**
     * 手机号码验证
     * @param phone
     * @return
     */
    public static boolean verifyNumberPhone(@NonNull String phone) {
        Pattern pattern = Pattern.compile(RegexConstant.MOBILE_PHONE.regex);
        boolean result = pattern.matcher(phone).matches();
        if(!result){
            ToastUtils.showToast(RegexConstant.MOBILE_PHONE.text);
        }
        return result;
    }
}
