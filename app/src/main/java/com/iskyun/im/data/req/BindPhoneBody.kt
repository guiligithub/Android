package com.iskyun.im.data.req

data class BindPhoneBody(
    var code: String = "",
    var phone: String = "",
    var type: Int = 0,
)