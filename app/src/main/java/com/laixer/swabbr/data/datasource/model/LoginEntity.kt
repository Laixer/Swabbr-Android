package com.laixer.swabbr.data.datasource.model

import com.laixer.swabbr.domain.model.Login
import com.laixer.swabbr.domain.model.PushNotificationPlatform
import com.squareup.moshi.Json

data class LoginEntity(
    @field:Json(name = "email") val email: String,
    @field:Json(name = "password") val password: String,
    @field:Json(name = "rememberMe") val rememberMe: Boolean,
    @field:Json(name = "pushNotificationPlatform") val pushNotificationPlatform: String,
    @field:Json(name = "handle") val handle: String
)

fun Login.mapToData(): LoginEntity = LoginEntity(
    email,
    password,
    remember,
    pushNotificationPlatform.value,
    handle
)

fun LoginEntity.mapToDomain(): Login = Login(
    email,
    password,
    rememberMe,
    PushNotificationPlatform.values().first { it.value == pushNotificationPlatform },
    handle
)

fun List<LoginEntity>.mapToDomain(): List<Login> = map { it.mapToDomain() }
fun List<Login>.mapToData(): List<LoginEntity> = map { it.mapToData() }
