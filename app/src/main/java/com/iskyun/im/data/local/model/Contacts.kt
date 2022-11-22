package com.iskyun.im.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity

/**
 * 联系人表
 */
@Entity(tableName = "contacts")
class Contacts {
    @ColumnInfo(name="user_id")
    var userId = 0
    @ColumnInfo(name="nick_name")
    var nickName = ""
    @ColumnInfo(name="avatar")
    var avatar = ""

//    @ColumnInfo(name="last_msg_time")
//    var lastMessageTime=""
//    @ColumnInfo(name="last_msg_content")
//    var lastMessageContent = ""//最后聊天内容
//    var noReadMsgCount=0//未查看消息数量
}