package com.iskyun.im.data.bean;

data class Gift(
    var darkDeduct: Int = 0,
    var giftName: String = "",
    var giftNum: Int = 0,
    var giftOriginalCost: Int = 0,
    var giftPic: String = "",
    var giftPrice: Int = 0,
    var giftType: Int = 0,
    var id: String = "",
    var isSpecialEffects: Int,// 是否特殊效果 0,1  0 否， 1是
    var earnings: Double = 0.0,//主播收益
    var specialPic: String = ""
)
