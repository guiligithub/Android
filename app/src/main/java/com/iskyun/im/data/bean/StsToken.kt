package com.iskyun.im.data.bean

class StsToken {
    var requestId: String = ""

    var assumedRoleUser: RoleUser? = null

    var credentials: Credentials? = null

    class Credentials {
        var securityToken: String = ""
        var accessKeySecret: String = ""
        var accessKeyId: String = ""
        var expiration: String? = ""
    }

    class RoleUser {
        var arn: String = ""
        var assumedRoleId: String = ""
    }

}