package com.iskyun.im.data.bean

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
open class Recharge(
    var id: String = "",
    var createBy: String = "",
    var createTime: String = "",
    var updateBy: String = "",
    var updateTime: String = "",
) : Parcelable

/**
 * 充值类型
 */
enum class RechargeType(val type: Int) {
    Diamond(1),
    Vip(2)
}