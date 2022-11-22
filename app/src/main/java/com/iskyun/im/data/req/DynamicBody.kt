package com.iskyun.im.data.req

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.iskyun.im.BR
import com.iskyun.im.data.bean.Dynamic

open class DynamicBody : BaseObservable() {
    @Bindable
    var address: String = ""
        set(address) {
            field = address
            notifyPropertyChanged(BR.address)
        }
    var authStatus: Int = 0

    @Bindable
    var lookType: Int = 0
        set(lookType) {
            field = lookType
            notifyPropertyChanged(BR.lookType)
        }

    @Bindable
    var textDetails: String = ""
        set(textDetails) {
            field = textDetails
            notifyPropertyChanged(BR.textDetails)
        }

    @Bindable
    var appCommonFileList: List<Dynamic.FileType>? = null
        set(appCommonFileList) {
            field = appCommonFileList
            notifyPropertyChanged(BR.appCommonFileList)
        }
    var userId: Int = 0

    var fileType :Int = 0

}

enum class FileUploadType(val uploadType:Int){
    //图片
    IMAGE(1),
    //视频
    VIDEO(2)
}

enum class DynamicLookType(val lookType: Int){
    OPEN(1),
    PRIVACY(2),
    ATTENTION(3),
    FANS(4)
}