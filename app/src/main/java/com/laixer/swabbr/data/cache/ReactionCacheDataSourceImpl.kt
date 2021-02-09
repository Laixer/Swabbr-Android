package com.laixer.swabbr.data.cache

import com.laixer.cache.Cache
import com.laixer.swabbr.data.interfaces.ReactionCacheDataSource
import com.laixer.swabbr.domain.model.Reaction
import io.reactivex.Single
import java.util.*

/**
 *  Caching for reaction objects.
 */
class ReactionCacheDataSourceImpl constructor(
    private val cache: Cache
) : ReactionCacheDataSource {
    override fun get(vlogId: UUID): Single<List<Reaction>> = cache.load(key + vlogId)

    override fun set(vlogId: UUID, list: List<Reaction>): Single<List<Reaction>> = cache.save(key + vlogId, list)
}
