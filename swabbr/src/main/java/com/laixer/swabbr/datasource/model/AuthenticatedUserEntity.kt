package com.laixer.swabbr.datasource.model

import com.squareup.moshi.Json

data class AuthenticatedUserEntity(
    @field:Json(name = "accessToken") val accessToken: String,
    @field:Json(name = "user") val user: UserEntity,
    @field:Json(name = "userSettings") val userSettings: SettingsEntity
)
