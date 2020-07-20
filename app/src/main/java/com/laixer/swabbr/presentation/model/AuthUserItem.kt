package com.laixer.swabbr.presentation.model

import com.auth0.android.jwt.JWT
import com.laixer.swabbr.domain.model.AuthUser

data class AuthUserItem(
    val accessToken: JWT,
    val settings: SettingsItem,
    val user: UserItem
)

fun AuthUser.mapToPresentation(): AuthUserItem =
    AuthUserItem(
        this.accessToken,
        this.userSettings.mapToPresentation(),
        this.user.mapToPresentation()
    )

fun AuthUserItem.hasValidSession(): Boolean = this.accessToken.isExpired(0L)
