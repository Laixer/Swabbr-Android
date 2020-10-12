package com.laixer.swabbr.data.datasource.cache

import com.laixer.cache.Cache
import com.laixer.swabbr.data.datasource.VlogCacheDataSource
import com.laixer.swabbr.domain.model.Vlog
import io.reactivex.Single
import java.util.UUID

class VlogCacheDataSourceImpl constructor(
    private val cache: Cache
) : VlogCacheDataSource {

    override fun getUserVlogs(userId: UUID): Single<List<Vlog>> =
        cache.load<List<Vlog>>(key).map { list -> list.filter { it.data.userId == userId } }

    override fun get(vlogId: UUID): Single<Vlog> = cache.load<List<Vlog>>(key).map { list -> list.first { it.data.id == vlogId } }

    override fun getRecommendedVlogs(): Single<List<Vlog>> = cache.load(recommendedKey)

    override fun setRecommendedVlogs(list: List<Vlog>): Single<List<Vlog>> = cache.save(recommendedKey, list)

    override fun set(item: Vlog): Single<Vlog> =
        cache.load<List<Vlog>>(key).map { list -> list.filter { it.data.id != item.data.id }.plus(item) }.flatMap { set(it) }.map { item }

    override fun set(list: List<Vlog>): Single<List<Vlog>> = cache.save(key, list)
}
