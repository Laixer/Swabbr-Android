package com.laixer.swabbr.presentation.model

import com.laixer.swabbr.domain.model.Settings

data class SettingsItem(
    var private: Boolean,
    var dailyVlogRequestLimit: Int,
    var followMode: Int
)

fun Settings.mapToPresentation(): SettingsItem =
    SettingsItem(
        this.private,
        this.dailyVlogRequestLimit,
        this.followMode
    )

fun SettingsItem.mapToDomain(): Settings =
    Settings(
        this.private,
        this.dailyVlogRequestLimit,
        this.followMode
    )
