package com.laixer.swabbr.data.cache

import com.laixer.cache.Cache
import com.laixer.swabbr.data.interfaces.VlogCacheDataSource
import com.laixer.swabbr.domain.model.Vlog
import io.reactivex.Completable
import io.reactivex.Single
import java.util.*

/**
 *  Caching for vlog entities.
 */
class VlogCacheDataSourceImpl constructor(
    private val cache: Cache
) : VlogCacheDataSource {
    override fun getForUser(userId: UUID): Single<List<Vlog>> =
        cache.load<List<Vlog>>(key).map { list -> list.filter { it.userId == userId } }

    override fun get(vlogId: UUID): Single<Vlog> =
        cache.load<List<Vlog>>(key).map { list -> list.first { it.id == vlogId } }

    override fun getRecommendedVlogs(): Single<List<Vlog>> = cache.load(keyRecommended)

    override fun setRecommendedVlogs(list: List<Vlog>): Single<List<Vlog>> = cache.save(keyRecommended, list)

    override fun set(item: Vlog): Single<Vlog> =
        cache.load<List<Vlog>>(key).map { list -> list.filter { it.id != item.id }.plus(item) }
            .flatMap(::set).map { item }

    override fun set(list: List<Vlog>): Single<List<Vlog>> = cache.save(key, list)

    override fun delete(vlogId: UUID): Completable = Completable.fromSingle(
        cache.load<MutableList<Vlog>>(key)
            .map { list -> cache.save(key, list.apply { removeAll { it.id === vlogId } }) })
}
