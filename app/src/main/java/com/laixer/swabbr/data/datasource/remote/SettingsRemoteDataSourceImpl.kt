package com.laixer.swabbr.data.datasource.remote

import com.laixer.swabbr.data.datasource.SettingsRemoteDataSource
import com.laixer.swabbr.data.datasource.model.mapToData
import com.laixer.swabbr.data.datasource.model.mapToDomain
import com.laixer.swabbr.data.datasource.model.remote.SettingsApi
import com.laixer.swabbr.domain.model.Settings
import io.reactivex.Single

class SettingsRemoteDataSourceImpl constructor(
    val api: SettingsApi
) : SettingsRemoteDataSource {

    override fun get(): Single<Settings> = api.get()
        .map { it.mapToDomain() }

    override fun set(settings: Settings): Single<Settings> = api.set(settings.mapToData())
        .map { it.mapToDomain() }
}
