package com.laixer.swabbr.datasource.cache

import com.laixer.cache.ReactiveCache
import com.laixer.swabbr.data.datasource.ReactionCacheDataSource
import com.laixer.swabbr.domain.model.Reaction
import io.reactivex.Single

class ReactionCacheDataSourceImpl constructor(
    private val cache: ReactiveCache<List<Reaction>>
) : ReactionCacheDataSource {

    val key = "Reaction List"

    override fun get(vlogId: String): Single<List<Reaction>> =
        cache.load(key + vlogId)

    override fun set(vlogId: String, list: List<Reaction>): Single<List<Reaction>> =
        cache.save(key + vlogId, list)
}
