package com.laixer.swabbr.data.repository

import com.laixer.swabbr.data.datasource.ReactionCacheDataSource
import com.laixer.swabbr.data.datasource.ReactionRemoteDataSource
import com.laixer.swabbr.domain.model.Reaction
import com.laixer.swabbr.domain.repository.ReactionRepository
import io.reactivex.Single
import java.util.UUID

class ReactionRepositoryImpl constructor(
    private val cacheDataSource: ReactionCacheDataSource,
    private val remoteDataSource: ReactionRemoteDataSource
) : ReactionRepository {

    override fun get(vlogId: UUID, refresh: Boolean): Single<List<Reaction>> = when (refresh) {
        true -> remoteDataSource.get(vlogId).flatMap { cacheDataSource.set(vlogId, it) }
        false -> cacheDataSource.get(vlogId).onErrorResumeNext { get(vlogId, true) }
    }
}
