package com.iskyun.im.data.req


class CallRecordBody {
    var callState: Int = 0//通话状态 1拨打 2接听
    var isResponse: Int = 0//是否响应
    var tag: Int = 0//消费类型 1文字 2图片 3语音 4视频
    var time: Int = 0//消费时长/秒
    var userId: Int = 0//主播id
}