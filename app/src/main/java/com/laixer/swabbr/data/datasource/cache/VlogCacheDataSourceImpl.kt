package com.laixer.swabbr.data.datasource.cache

import com.laixer.cache.ReactiveCache
import com.laixer.swabbr.data.datasource.VlogCacheDataSource
import com.laixer.swabbr.domain.model.Vlog
import io.reactivex.Single
import java.util.UUID

class VlogCacheDataSourceImpl constructor(
    private val cache: ReactiveCache<List<Vlog>>
) : VlogCacheDataSource {

    override fun getUserVlogs(userId: UUID): Single<List<Vlog>> =
        cache.load(key).map { list -> list.filter { it.userId == userId } }

    override fun get(vlogId: UUID): Single<Vlog> = cache.load(key).map { list -> list.first { it.id == vlogId } }

    override fun getRecommendedVlogs(): Single<List<Vlog>> = cache.load(recommendedKey)

    override fun setRecommendedVlogs(list: List<Vlog>): Single<List<Vlog>> = cache.save(recommendedKey, list)

    override fun set(item: Vlog): Single<Vlog> =
        cache.load(key).map { list -> list.filter { it.id != item.id }.plus(item) }.flatMap { set(it) }.map { item }

    override fun set(list: List<Vlog>): Single<List<Vlog>> = cache.save(key, list)
}
