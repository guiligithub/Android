package com.iskyun.im.data.bean

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * 赠送礼物返回
 */
@Parcelize
data class GiveGiftResult(
    var id: String = "",
    var organizationId: String = "",
    var giveUserId: Int = 0,
    var receiveUserId: Int = 0,
    var virtualCurrency: Int = 0,
    var earningsRatio: Double = 0.0,
    var earnings: Double = 0.0,//主播收益
    var earningsType: Int = 0,
    var goodsId: String = "",
    var orderId: String = "",
    var recordType: Int = 0,
):Parcelable