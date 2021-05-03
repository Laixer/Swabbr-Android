package com.laixer.swabbr.data.model

import com.laixer.swabbr.domain.model.Login
import com.squareup.moshi.Json

/**
 * Entity used for logging in. This is only sent to the server.
 */
data class LoginEntity(
    @field:Json(name = "email") val email: String,
    @field:Json(name = "password") val password: String,
    @field:Json(name = "rememberMe") val rememberMe: Boolean,
    @field:Json(name = "pushNotificationPlatform") val pushNotificationPlatform: Int,
    @field:Json(name = "handle") val handle: String
)

/**
 * Map a login wrapper from domain to data.
 */
fun Login.mapToData(): LoginEntity = LoginEntity(
    email,
    password,
    remember,
    pushNotificationPlatform.ordinal,
    handle
)
