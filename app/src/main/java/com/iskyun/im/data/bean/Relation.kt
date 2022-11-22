package com.iskyun.im.data.bean

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.iskyun.im.BR

class Relation : BaseObservable() {
    @Bindable
    var visitorNum: Int = 0
        set(visitorNum) {
            field = visitorNum
            notifyPropertyChanged(BR.visitorNum)
        }
    @Bindable
    var followNum: Int = 0
        set(followNum) {
            field = followNum
            notifyPropertyChanged(BR.followNum)
        }
    @Bindable
    var fansNum: Int = 0
        set(fansNum) {
            field = fansNum
            notifyPropertyChanged(BR.fansNum)
        }
    @Bindable
    var addFollowNum: Int = 0
        set(addFollowNum) {
            field = addFollowNum
            notifyPropertyChanged(BR.addFollowNum)
        }
    @Bindable
    var addFansNum: Int = 0
        set(addFansNum) {
            field = addFansNum
            notifyPropertyChanged(BR.addFansNum)
        }
    @Bindable
    var addVisitorNum: Int = 0
        set(addVisitorNum) {
            field = addVisitorNum
            notifyPropertyChanged(BR.addVisitorNum)
        }
}

enum class RelationType(val relationType: Int) {
    // 1来访 2关注 3粉丝或拉黑
    VISIT(1),
    ATTENTION(2),
    BLOCK(3),
}