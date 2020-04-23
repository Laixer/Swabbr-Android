package com.laixer.swabbr.presentation.model

import com.laixer.swabbr.domain.model.Login
import com.laixer.swabbr.domain.model.PushNotificationPlatform

data class LoginItem(
    val username: String,
    val password: String,
    val remember: Boolean,
    val platform: PushNotificationPlatform,
    val handle: String
)

fun LoginItem.mapToDomain(): Login = Login(
    this.username,
    this.password,
    this.remember,
    this.platform,
    this.handle
)
