package com.iskyun.im.data.bean

import com.iskyun.im.data.req.DynamicBody

class Dynamic {
    var id: String = ""
    var createBy: String = ""
    var createTime: String = ""
    var updateBy: String = ""
    var updateTime: String = ""
    var userId: Int = 0
    var textDetails: String = ""
    var address: String = ""
    var authStatus: Int = 0
    var lookType: Int = 0
    var fileType: Int = 0
    var appCommonFileList: List<FileType> = ArrayList()
    var nickname: String = ""
    var age: Int = 0;
    var headUrl: String = ""
    var isAttention: Boolean = false
    var isCommend: Boolean = false

    var sex: Int = 0
    var commendNum: Int = 0

    class FileType {
//        var fileType: String = ""
        var fileUrl: String = ""
    }
}