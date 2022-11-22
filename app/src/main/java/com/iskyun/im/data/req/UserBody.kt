package com.iskyun.im.data.req


class UserBody {
    var id: Int = 0
    var nickname: String? = null //
    var headUrl: String? = null //头像
    var sex: Int = 1 //性别：0.女、1.男（默认） 不可修改\n
    var age: Int = 0 //年龄
    var profession: String? = null
    var city: String? = null
    var height: Int = 0 //身高
    var affectiveState: Int = 1
    var tag: String? = null //个性标签 最多选取三个\n
    var signature: String? = null //签名 最多20字 需审核?
    var wxNumber: String = ""
}