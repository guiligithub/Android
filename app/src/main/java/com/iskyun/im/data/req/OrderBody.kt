package com.iskyun.im.data.req

import cn.tillusory.sdk.net.S

/**
 * orderType
 * 1  钻石
 * 2  VIP
 */
data class OrderBody(
    var id : String = "",
    var orderType:Int=1
)