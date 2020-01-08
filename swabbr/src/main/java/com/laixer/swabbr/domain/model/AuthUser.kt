package com.laixer.swabbr.domain.model

data class AuthUser(
    val accessToken: String,
    val user: User,
    val userSettings: Settings
)
