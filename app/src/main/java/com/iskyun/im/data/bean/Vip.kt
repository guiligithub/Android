package com.iskyun.im.data.bean

data class Vip(
    val vipPrice: Double,
    val vipTime: Int,
    val vipType: Int,
): Recharge()

enum class VipType(val type :Int){
    VIP_OF_C(1),
    VIP_OF_S(2)
}