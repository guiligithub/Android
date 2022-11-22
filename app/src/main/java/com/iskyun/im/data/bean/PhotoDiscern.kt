package com.iskyun.im.data.bean

data class PhotoDiscern(
    val msg: String = "",
    val code: Int = 0,
    val dataId: String = "",
    val results: List<DiscernResult> = ArrayList(),
    val url: String = "",
    val taskId: String = "",
)

class DiscernResult {
    val rate: Float = 0f
    val suggestion: String = ""//pass  通过
    val label: String = ""
    val scene: String = ""

    companion object {
        const val PASS:String = "pass"
    }
}