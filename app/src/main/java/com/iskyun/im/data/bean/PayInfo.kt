package com.iskyun.im.data.bean

data class PayInfo(
    val packageValue: String,
    val appId: String,
    val prepayId: String,
    val partnerId: String,
    val nonceStr: String,
    val timeStamp: String,
    val paySign: String,
    val signType: String,
)