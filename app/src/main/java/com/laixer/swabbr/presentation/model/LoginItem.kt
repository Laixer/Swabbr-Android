package com.laixer.swabbr.presentation.model

import com.laixer.swabbr.domain.model.Login
import com.laixer.swabbr.domain.types.PushNotificationPlatform

/**
 *  Item representing a user login wrapper.
 */
data class LoginItem(
    val email: String,
    val password: String,
    val remember: Boolean,
    val platform: PushNotificationPlatform,
    val handle: String
)

/**
 *  Map a login item from presentation to domain.
 */
fun LoginItem.mapToDomain(): Login = Login(
    this.email,
    this.password,
    this.remember,
    this.platform,
    this.handle
)
