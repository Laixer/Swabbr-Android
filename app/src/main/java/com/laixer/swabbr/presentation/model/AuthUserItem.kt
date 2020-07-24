package com.laixer.swabbr.presentation.model

import com.auth0.android.jwt.JWT
import com.laixer.swabbr.domain.model.AuthUser

data class AuthUserItem(
    val jwtToken: JWT,
    val settings: SettingsItem,
    val user: UserItem
)

fun AuthUser.mapToPresentation(): AuthUserItem =
    AuthUserItem(
        JWT(this.jwtToken),
        this.userSettings.mapToPresentation(),
        this.user.mapToPresentation()
    )

fun AuthUserItem.hasValidSession(): Boolean = !this.jwtToken.isExpired(0L)
