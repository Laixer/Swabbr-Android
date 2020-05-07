package com.laixer.swabbr.data.datasource.remote

import com.laixer.swabbr.data.datasource.SettingsRemoteDataSource
import com.laixer.swabbr.datasource.model.mapToData
import com.laixer.swabbr.datasource.model.mapToDomain
import com.laixer.swabbr.datasource.model.remote.SettingsApi
import com.laixer.swabbr.domain.model.Settings
import io.reactivex.Completable
import io.reactivex.Single

class SettingsRemoteDataSourceImpl constructor(
    val api: SettingsApi
) : SettingsRemoteDataSource {

    override fun get(): Single<Settings> = api.get().map { it.mapToDomain() }

    override fun set(settings: Settings): Completable = api.set(settings.mapToData())
}
