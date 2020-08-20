package com.laixer.swabbr.data.datasource.model

import com.laixer.swabbr.domain.model.FollowMode
import com.laixer.swabbr.domain.model.Settings
import com.squareup.moshi.Json

data class SettingsEntity(
    @field:Json(name = "isPrivate") val private: Boolean,
    @field:Json(name = "dailyVlogRequestLimit") val dailyVlogRequestLimit: Int,
    @field:Json(name = "followMode") val followMode: String
)

fun SettingsEntity.mapToDomain(): Settings = Settings(
    private,
    dailyVlogRequestLimit,
    FollowMode.values().first { it.value == followMode }
)

fun Settings.mapToData(): SettingsEntity = SettingsEntity(
    private,
    dailyVlogRequestLimit,
    followMode.value
)

fun List<Settings>.mapToData(): List<SettingsEntity> = map { it.mapToData() }
fun List<SettingsEntity>.mapToDomain(): List<Settings> = map { it.mapToDomain() }
