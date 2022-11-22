package com.iskyun.im.data.bean

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.iskyun.im.BR
import java.io.Serializable

/**
 * 证件正反两面
 */
class IdCard : BaseObservable(), Serializable{
    @Bindable
    var idCardBackUrl: String = ""
        set(idCardBackUrl) {
            field = idCardBackUrl
            notifyPropertyChanged(BR.idCardBackUrl);
        }

    @Bindable
    var idCardFaceUrl: String = ""
        set(idCardFaceUrl) {
            field = idCardFaceUrl
            notifyPropertyChanged(BR.idCardFaceUrl);
        }
    var authStatus: Int = 0
}