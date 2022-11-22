package com.iskyun.im.data.bean

import android.os.Parcelable
import androidx.databinding.BaseObservable
import com.iskyun.im.data.req.AnchorPriceSetting
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class AnchorAuth(
    var authDetails: String = "",
    var authStatus: Int = 0,
    var id: String = "",
    var idCardBackUrl: String = "",
    var idCardFaceUrl: String = "",
    var laborCode: Int = 0,
    var userId: Int = 0,
    var videoAuthUrl: String = "",
    var appUserSetting: @RawValue AnchorPriceSetting? = null
) : BaseObservable(), Parcelable
