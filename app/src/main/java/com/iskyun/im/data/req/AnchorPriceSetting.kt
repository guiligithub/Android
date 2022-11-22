package com.iskyun.im.data.req

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.iskyun.im.BR

open class AnchorPriceSetting : BaseObservable() {
    @Bindable
    var videoMinute: Int? = null
        set(videoMinute) {
            field = videoMinute;
            notifyPropertyChanged(BR.videoMinute);
        }

    @Bindable
    var voiceMinute: Int? = null
        set(voiceMinute) {
            field = voiceMinute;
            notifyPropertyChanged(BR.voiceMinute);
        }

    @Bindable
    var textPrice: Int? = null
        set(textPrice) {
            field = textPrice;
            notifyPropertyChanged(BR.textPrice);
        }

    @Bindable
    var unlockWechat: Int? = null
        set(unlockWechat) {
            field = unlockWechat;
            notifyPropertyChanged(BR.unlockWechat);
        }
}
