package com.iskyun.im.data.req;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import com.iskyun.im.BR

class LoginBody : BaseObservable() {

    //	登录账号
    @Bindable
    var loginInfo: String = ""
        set(loginInfo) {
            field = loginInfo
            notifyPropertyChanged(BR.loginInfo)
        }

    //登录方式,可用值:accountAndPassword,mobileAndCode
    var loginMethod: String = ""

    //登录密钥， 密码或手机验证码
    @Bindable
    var loginSecret: String = ""
        set(loginSecret) {
            field = loginSecret
            notifyPropertyChanged(BR.loginSecret)
        }

    //用户类型,枚举值为sys=平台,user=用户,可用值:sys,user
    var userType: String = ""

    //渠道
    var source: String = ""


    companion object {
        const val ACCOUNT_AND_PASSWORD = "accountAndPassword"
        const val MOBILE_AND_CODE = "mobileAndCode"


        const val USER = "user"
        const val SYS = "sys"
    }
}


