package com.laixer.swabbr.data.repository

import com.laixer.swabbr.data.datasource.SettingsCacheDataSource
import com.laixer.swabbr.data.datasource.SettingsRemoteDataSource
import com.laixer.swabbr.domain.model.Settings
import com.laixer.swabbr.domain.repository.SettingsRepository
import io.reactivex.Single

class SettingsRepositoryImpl constructor(
    private val cacheDataSource: SettingsCacheDataSource,
    private val remoteDataSource: SettingsRemoteDataSource
) : SettingsRepository {

    override fun get(refresh: Boolean): Single<Settings> =
        when (refresh) {
            true -> remoteDataSource.get()
                .flatMap { cacheDataSource.set(it) }
            false -> cacheDataSource.get()
                .onErrorResumeNext { get(true) }
        }

    override fun set(settings: Settings, updateRemote: Boolean): Single<Settings> =
        when (updateRemote) {
            true -> remoteDataSource.set(settings)
                .flatMap { cacheDataSource.set(it) }
            false -> cacheDataSource.set(settings)
        }
}
