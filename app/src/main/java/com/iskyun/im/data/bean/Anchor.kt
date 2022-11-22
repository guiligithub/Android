package com.iskyun.im.data.bean

import android.os.Parcelable
import com.iskyun.im.R
import com.iskyun.im.utils.DisplayUtils
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
class Anchor(
    var photoUrls: List<String> = ArrayList(),
    var textPrice: Int = 0,
    var imgPrice:Int? =null,
    var age: Int? = null,
    var affectiveState: Int = 0,

    var city: String? = null,
    var coverVideoUrl: String? = null,
    var createTime: String = "",
    var evaruates: List<String>? = null,
    var fansNum: Int = 0,
    var followNum: Int = 0,
    var headUrl: String? = null,
    var height: String? = null,
    var id: Int = 0,
    var isAnchor: Int = 0,
    var isOnline: Int = 0,
    var isVip: Int = 0,
    var vipType: Int = 0,
    var lastLoginTime: String? = null,
    var level: Int = 0,
    var nickname: String? = null,
    var profession: String? = null,
    var sex: Int = 0,
    var signature: String? = null,
    var state: Int = 0,
    var tag: String? = null,
    var updateTime: String? = null,
    var vipStartTime: String? = null,
    var vipExpireTime: String? = null,
    var userDiamond: Int = 0,
    var videoMinute: Int = 0,
    var visitorNum: Int = 0,
    var voiceMinute: Int = 0,
    var wxId: String? = null,
    var wxNickname: String? = null,
    var evaluates:List<String> = ArrayList(),
    var starLevel:Int? = null,
    var wxNumber:String = "",
    var isFocus:Int =0,//0 no  1 yes
    var isBlack:Int =0,//0  1

    @get:JvmName("isUnlockWechat")
    @set:JvmName("setIsUnlockWechat")
    var isUnlockWechat:Int =0,

    var unlockWechat:Int =0,

    var wechatPrice:Int=0,//微信价格

    var intimateNum:Double =0.0,//亲密度

    var userBalance: Double = 0.0 //元宝数量



) : Parcelable

enum class Tag(var title: String, var tag: Int) {
    NEWS(DisplayUtils.getString(R.string.news), 1),

    //NEARBY(DisplayUtils.getString(R.string.nearby),1),
    RECOMMEND(DisplayUtils.getString(R.string.recommend), 2),
    THREE(DisplayUtils.getString(R.string.three), 3),
    FOUR(DisplayUtils.getString(R.string.four), 4),
    FIVE(DisplayUtils.getString(R.string.five), 5),
}

enum class OnlineStatus(val status: Int) {
    ONLINE(1),
    OFFLINE(0),
    ON_BACKGROUND(3),
    ON_CALL(4),
}

/**
 * 审核状态
 *未审批 1
 *已通过 2
 *未通过 3
 */
enum class AuthStatus(val authStatus: Int) {
    NO_APPROVAL(1),//
    ON_PASS(2),
    UN_PASS(3);
}