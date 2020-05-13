package com.laixer.swabbr.data.datasource

import com.laixer.swabbr.domain.model.Settings
import io.reactivex.Completable
import io.reactivex.Single

interface SettingsCacheDataSource {

    val key: String
        get() = "SETTINGS"

    fun get(): Single<Settings>

    fun set(settings: Settings): Single<Settings>
}

interface SettingsRemoteDataSource {

    fun get(): Single<Settings>

    fun set(settings: Settings): Single<Settings>
}
