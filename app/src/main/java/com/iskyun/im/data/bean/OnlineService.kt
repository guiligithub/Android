package com.iskyun.im.data.bean

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "online_service",
    primaryKeys = ["id"],
    indices = [Index(value = ["id"], unique = true)]
)
/**
 * 在线客服
 */
class OnlineService {
    var nickname: String = ""
    var id: String = ""
    var accountType: Int = 0 //1QQ 2 微信
    var account:String=""
}