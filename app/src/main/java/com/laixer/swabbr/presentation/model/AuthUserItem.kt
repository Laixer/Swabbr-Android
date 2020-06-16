package com.laixer.swabbr.presentation.model

import com.laixer.swabbr.domain.model.AuthUser

data class AuthUserItem(
    val accessToken: String?,
    val settings: SettingsItem,
    val user: UserItem
)

fun AuthUser.mapToPresentation(): AuthUserItem =
    AuthUserItem(
        this.accessToken,
        this.userSettings.mapToPresentation(),
        this.user.mapToPresentation()
    )

fun AuthUserItem.isLoggedIn(): Boolean = this.accessToken != null
