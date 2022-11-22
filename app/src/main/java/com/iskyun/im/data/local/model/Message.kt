package com.iskyun.im.data.local.model

import androidx.room.Entity
import androidx.room.Index

@Entity(tableName = "message", indices = [Index(value = ["anchorId"])])
class Message {
    @androidx.room.PrimaryKey(autoGenerate = true)
    var msgId=0
    var msgType=0//聊天类型  0 文本， 1 语音 2 图片
//    var is
    var content=""
    var createTime=""
    val anchorId = 0
    val uId = 0
}