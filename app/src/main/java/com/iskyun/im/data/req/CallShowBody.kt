package com.iskyun.im.data.req


class CallShowBody {
    var authStatus: Int = 0
    var effectId: String = ""
    var effectType: Int = 0
    var fileUrl: String = ""
    var id: String = ""
    var nickname: String = ""
    var userId: Int = 0
}

enum class EffectType(val effectType:Int){
    PHOTO_WALL(1),//照片墙
    DYNAMIC(2),//动态
    FEEDBACK(5),//反馈
    GREET(6),//招呼
    CALL_SHOW(7),//来电秀
}