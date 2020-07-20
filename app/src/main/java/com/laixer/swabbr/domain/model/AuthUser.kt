package com.laixer.swabbr.domain.model

import com.auth0.android.jwt.JWT

data class AuthUser(
    val accessToken: JWT,
    var user: User,
    var userSettings: Settings
)
