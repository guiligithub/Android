package com.iskyun.im.data.req

class ChatRecordBody{
    var tag :Int=0//消费类型 1文字 2图片 3语音 4视频
    var time:Int=0//消费时常  s
    var userId:Int=0//主播id

    var callState:Int=0//通话状态 1拨打 2接听
    var isFoc:Int=0//是否免费次数 0否1是
}