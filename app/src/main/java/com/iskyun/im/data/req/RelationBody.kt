package com.iskyun.im.data.req;

data class RelationBody(
    var relationType: Int = 0,
    var startUserId: Int = 0,
    var targetUserId: Int = 0
)
