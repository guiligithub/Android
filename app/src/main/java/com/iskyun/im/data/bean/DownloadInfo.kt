package com.iskyun.im.data.bean

import java.io.File

class DownloadInfo {
    var file: File? = null
    var fileName: String? = null

    //单位 byte
    var fileSize: Long = 0

    //当前已下载大小
    var currentSize: Long = 0

    //当前下载进度
    var progress = 0

    //下载速率
    var speed: Long = 0

    //下载用时
    var usedTime: Long = 0

    //下载异常信息
    var errorMsg: Throwable? = null
}