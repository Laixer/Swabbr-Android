package com.laixer.swabbr.domain.model

data class AuthUser(
    val jwtToken: String,
    var user: User,
    var userSettings: Settings
)
