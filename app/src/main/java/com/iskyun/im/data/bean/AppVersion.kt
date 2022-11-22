package com.iskyun.im.data.bean

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * 版本
 */
@Parcelize
data class AppVersion(
    val versions: String = "",
    var downloadUrl: String = "",
    val updateContent: String = "",
    val versionCode: Int = 0,
    val isMust : Boolean = false
):Parcelable