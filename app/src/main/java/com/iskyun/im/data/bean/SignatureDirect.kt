package com.iskyun.im.data.bean

data class SignatureDirect(
    val accessid : String,
    val policy : String,
    val signature : String,
    val dir : String,
    val host : String,
    val expire : String,
)
