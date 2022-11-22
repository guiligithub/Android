package com.iskyun.im.data.bean

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.iskyun.im.BR


open class User : BaseObservable() {
    var id: Int = 0
    var createBy: String? = null //创建人
    var createTime: String? = null //创建日期 date-time
    var updateBy: String? = null //更新人
    var updateTime: String? = null//	更新日期 date-time

    @Bindable
    var nickname: String? = null //
        set(nickname) {
            field = nickname
            notifyPropertyChanged(BR.nickname)
        }

    @Bindable
    var realname: String? = null //真实姓名(预留)
        set(realname) {
            field = realname
            notifyPropertyChanged(BR.realname)
        }

    @Bindable
    var mobile: String = "" //手机
        set(mobile) {
            field = mobile
            notifyPropertyChanged(BR.mobile)
        }
    var password: String = "" //密码
    var state: Int = 0 //逻辑状态：0.禁用、1.启用

    @Bindable
    var headUrl: String? = null //头像
        set(headUrl) {
            field = headUrl
            notifyPropertyChanged(BR.headUrl)
        }

    @Bindable
    var sex: Int = 1 //性别：0.女、1.男（默认） 不可修改\n
        set(sex) {
            field = sex
            notifyPropertyChanged(BR.sex)
        }

    @Bindable
    var age: Int = 0 //年龄
        set(age) {
            field = age
            notifyPropertyChanged(BR.age)
        }

    @Bindable
    var profession: String? = null //职业
        set(profession) {
            field = profession
            notifyPropertyChanged(BR.profession)
        }

    @Bindable
    var city: String? = null //所在城市
        set(city) {
            field = city
            notifyPropertyChanged(BR.city)
        }

    @Bindable
    var height: Int = 0 //身高
        set(height) {
            field = height
            notifyPropertyChanged(BR.height)
        }

    @Bindable
    var affectiveState: Int = 1
        //	情感状态\n单身(默认) 恋爱中 已婚
        set(affectiveState) {
            field = affectiveState
            notifyPropertyChanged(BR.affectiveState)
        }

    @Bindable
    var tag: String? = null //个性标签 最多选取三个\n
        set(tag) {
            field = tag
            notifyPropertyChanged(BR.tag)
        }

    @Bindable
    var signature: String? = null //签名 最多20字 需审核?
        set(signature) {
            field = signature
            notifyPropertyChanged(BR.signature)
        }
    @Bindable
    var fansNum: Int = 0 //粉丝数
        set(fansNum) {
            field = fansNum;
            notifyPropertyChanged(BR.fansNum)
        }
    @Bindable
    var followNum: Int = 0 //关注数
        set(followNum) {
            field = followNum;
            notifyPropertyChanged(BR.followNum)
        }
    @Bindable
    var visitorNum: Int = 0 //访客数
        set(visitorNum) {
            field = visitorNum;
            notifyPropertyChanged(BR.visitorNum)
        }
    var isVip: Int = 0 //是否VIP
    var vipType: Int = 0//1普通vip，2尊贵vip
    var vipStartTime: String? = null //vip开始时间
    var vipExpireTime: String? = null //vip到期时间
    var level: Int = 0 //等级体系
    var lastLoginTime: String? = null //最后一次登录时间
    var wxId: String? = null //微信id
    var wxNickname: String? = null //微信名称
    @Bindable
    var userDiamond: Int = 0 //钻石数量
        set(userDiamond) {
            field = userDiamond;
            notifyPropertyChanged(BR.userDiamond)
        }
    var isAnchor: Int = 0 //是否主播

    @Bindable
    var accruingMoney: Double = 0.0 //累计充值金额
        set(accruingMoney) {
            field = accruingMoney;
            notifyPropertyChanged(BR.accruingMoney)
        }

    @Bindable
    var userBalance: Double = 0.0 //元宝数量
        set(userBalance) {
            field = userBalance;
            notifyPropertyChanged(BR.userBalance)
        }
    var organizationId: String = "" //所属工会
    var isOnline: Int = 0
    var starLevel: Int = 0//星级

    var wxNumber: String = ""

    @Bindable
    var videoMinute: Int = 0 //视频单价(钻石/分)
        set(videoMinute) {
            field = videoMinute
            notifyPropertyChanged(BR.videoMinute)
        }

    @Bindable
    var voiceMinute: Int = 0 //语音单价(钻石/分)
        set(voiceMinute) {
            field = voiceMinute
            notifyPropertyChanged(BR.voiceMinute)
        }
    var account: String = "" //账号(预留)

    @Bindable
    var coverVideoUrl: String? = null //封面视频
        set(coverVideoUrl) {
            field = coverVideoUrl
            notifyPropertyChanged(BR.coverVideoUrl)
        }

    //
    @Bindable
    var phoneShowUrl: String? = null //来电秀 (用于视频电话)
        set(phoneShowUrl) {
            field = phoneShowUrl
            notifyPropertyChanged(BR.phoneShowUrl)
        }
    var textPrice: Int = 0
    var imgPrice: Int = 0
    var unlockWechat: Int? = null
    var darkDeduct: Int? = null

}

enum class Sex {
    WOMAN, MAN
}
