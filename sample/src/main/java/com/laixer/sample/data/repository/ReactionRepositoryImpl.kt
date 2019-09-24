package com.laixer.sample.data.repository

import com.laixer.sample.data.datasource.ReactionCacheDataSource
import com.laixer.sample.data.datasource.ReactionRemoteDataSource
import com.laixer.sample.domain.model.Reaction
import com.laixer.sample.domain.repository.ReactionRepository
import io.reactivex.Single

class ReactionRepositoryImpl constructor(
    private val cacheDataSource: ReactionCacheDataSource,
    private val remoteDataSource: ReactionRemoteDataSource
) : ReactionRepository {

    override fun get(vlogId: String, refresh: Boolean): Single<List<Reaction>> =
        when (refresh) {
            true -> remoteDataSource.get(vlogId)
                .flatMap { cacheDataSource.set(vlogId, it) }
            false -> cacheDataSource.get(vlogId)
                .onErrorResumeNext { get(vlogId, true) }
        }
}
