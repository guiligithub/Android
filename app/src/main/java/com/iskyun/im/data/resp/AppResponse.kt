package com.iskyun.im.data.resp

data class AppResponse<T>(
    var data: T? = null,
    val msg: String = "",
    var code: Int = 0,
    var timestamp: String = ""
)
