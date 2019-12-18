package com.laixer.swabbr.datasource.cache

import com.laixer.cache.ReactiveCache
import com.laixer.swabbr.data.datasource.VlogCacheDataSource
import com.laixer.swabbr.domain.model.Vlog
import io.reactivex.Single

class VlogCacheDataSourceImpl constructor(
    private val cache: ReactiveCache<List<Vlog>>
) : VlogCacheDataSource {
    val key = "Vlog List"

    override fun get(): Single<List<Vlog>> =
        cache.load(key)

    override fun set(list: List<Vlog>): Single<List<Vlog>> =
        cache.save(key, list)

    override fun get(vlogId: String): Single<Vlog> =
        cache.load(key)
            .map { list -> list.first { it.id == vlogId } }

    override fun set(item: Vlog): Single<Vlog> =
        cache.load(key)
            .map { list -> list.filter { it.id != item.id }.plus(item) }
            .flatMap { set(it) }
            .map { item }
}
