package com.laixer.swabbr.datasource.model

import com.auth0.android.jwt.JWT
import com.laixer.swabbr.domain.model.AuthUser
import com.squareup.moshi.Json

data class AuthUserEntity(
    @field:Json(name = "token") val accessToken: String,
    @field:Json(name = "user") val user: UserEntity,
    @field:Json(name = "userSettings") val userSettings: SettingsEntity
)

fun AuthUserEntity.mapToDomain(): AuthUser = AuthUser(JWT(accessToken), user.mapToDomain(), userSettings.mapToDomain())
fun AuthUser.mapToData(): AuthUserEntity = AuthUserEntity(accessToken.toString(), user.mapToData(), userSettings.mapToData())

fun List<AuthUserEntity>.mapToDomain(): List<AuthUser> = map { it.mapToDomain() }
fun List<AuthUser>.mapToData(): List<AuthUserEntity> = map { it.mapToData() }
