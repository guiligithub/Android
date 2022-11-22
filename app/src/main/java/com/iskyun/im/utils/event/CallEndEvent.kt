package com.iskyun.im.utils.event

class CallEndEvent(
    var callType: Int = 0,
    var duration: Long = 0,
    var endReason: Int=0,//通话结束原因 详情看 EaseCallEndReason
)