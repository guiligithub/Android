package com.iskyun.im.data.resp;

class ListRecords<T> {
    var records: List<T>? = null
    var total: Int = 0
    var size: Int = 0
    var current: Int = 0
    var pages: Int = 0

    var optimizeCountSql: Boolean = false
    var searchCount: Boolean = false
    var countId: String = ""
    var maxLimit: String = ""
}
