package com.iskyun.im.utils;

public enum RegexConstant {

    //短信验证码，必须为数字，长度等于6位。
    AUTH_CODE("^\\d{4}$", "验证码输入有误"),

    PASSWORD("^\\d{6}$", "密码输入有误"),

    //手机号码
    MOBILE_PHONE("^1(3|4|5|6|7|8|9)\\d{9}$", "手机号码输入有误");

    public String regex;
    public String text;

    RegexConstant(String regex, String text) {
        this.regex = regex;
        this.text = text;
    }
}
