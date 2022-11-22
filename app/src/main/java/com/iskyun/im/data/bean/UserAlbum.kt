package com.iskyun.im.data.bean

data class UserAlbum(
    var authStatus: Int = 0,
    var createBy: String? = null,
    var createTime: String? = null,
    var effectId: String? = null,
    var effectType: Int = 0,
    var fileUrl: String? = null,
    var id: String? = null,
    var updateBy: String? = null,
    var updateTime: String? = null,
    var userId: Int = 0
)