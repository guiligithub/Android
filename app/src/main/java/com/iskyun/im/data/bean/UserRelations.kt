package com.iskyun.im.data.bean

import androidx.databinding.Bindable
import java.net.IDN
import java.sql.RowId
import java.sql.Time
import java.util.*

 class UserRelations(
         var id: Long,
         var createTime: String="",
         var isAnchor: Int=0,
         var userId: Int = 0,
         var nickname: String = "",
         var age: Int = 0,
         var headUrl: String = "",
         var targetUserId: Int = 0,
         var startUserId:Int=0,
         var sex:Int=0,
         var signature:String="",
         var isAttention: Boolean = false,


)