package com.laixer.swabbr.domain.model

import com.laixer.swabbr.domain.types.PushNotificationPlatform

/**
 * Wrapper for logging the user in.
 */
data class Login(
    val email: String,
    val password: String,
    val remember: Boolean,
    val pushNotificationPlatform: PushNotificationPlatform,
    val handle: String
)
