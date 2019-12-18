package com.laixer.swabbr.datasource.model

import com.laixer.swabbr.domain.model.Settings
import com.squareup.moshi.Json

data class SettingsEntity(
    @field:Json(name = "private") val private: Boolean,
    @field:Json(name = "dailyVlogRequestLimit") val dailyVlogRequestLimit: Int,
    @field:Json(name = "followMode") val followMode: Int
)

fun SettingsEntity.mapToDomain(): Settings = Settings(private, dailyVlogRequestLimit, followMode)

fun Settings.mapToData(): SettingsEntity = SettingsEntity(private, dailyVlogRequestLimit, followMode)
