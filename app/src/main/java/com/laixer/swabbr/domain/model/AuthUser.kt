package com.laixer.swabbr.domain.model

data class AuthUser(
    val accessToken: String?,
    var user: User,
    var userSettings: Settings
)
