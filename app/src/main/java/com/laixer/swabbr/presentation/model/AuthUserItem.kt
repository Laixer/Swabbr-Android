package com.laixer.swabbr.presentation.model

import com.auth0.android.jwt.JWT
import com.laixer.swabbr.domain.model.AuthUser

data class AuthUserItem(
    val jwtToken: JWT?,
    val user: UserItem,
    val settings: SettingsItem?
)

fun AuthUser.mapToPresentation(): AuthUserItem =
    AuthUserItem(
        this.jwtToken,
        this.user.mapToPresentation(),
        this.userSettings?.mapToPresentation()
    )

fun AuthUserItem.hasValidSession(): Boolean = this.jwtToken?.isExpired(0L)?.not() ?: false
