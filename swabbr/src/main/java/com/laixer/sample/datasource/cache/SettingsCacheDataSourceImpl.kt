package com.laixer.swabbr.datasource.cache

import com.laixer.cache.ReactiveCache
import com.laixer.swabbr.data.datasource.SettingsCacheDataSource
import com.laixer.swabbr.domain.model.Settings
import io.reactivex.Single

class SettingsCacheDataSourceImpl constructor(
    private val cache: ReactiveCache<Settings>
) : SettingsCacheDataSource {

    val key = "Settings"

    override fun get(): Single<Settings> =
        cache.load(key)

    override fun set(settings: Settings): Single<Settings> = cache.save(key, settings)

}
