package com.laixer.swabbr.data.repository

import com.laixer.swabbr.data.datasource.ReactionCacheDataSource
import com.laixer.swabbr.data.datasource.ReactionDataSource
import com.laixer.swabbr.domain.model.DatasetStats
import com.laixer.swabbr.domain.model.Reaction
import com.laixer.swabbr.domain.model.UploadWrapper
import com.laixer.swabbr.domain.repository.ReactionRepository
import io.reactivex.Completable
import io.reactivex.Single
import java.util.*

// TODO This doesn't use its cache.
/**
 *  Repository for reaction entities.
 */
class ReactionRepositoryImpl constructor(
    private val cacheDataSource: ReactionCacheDataSource,
    private val remoteDataSource: ReactionDataSource
) : ReactionRepository {

    override fun delete(reactionId: UUID): Completable = remoteDataSource.delete(reactionId)

    override fun generateUploadWrapper(): Single<UploadWrapper> = remoteDataSource.generateUploadWrapper()

    override fun get(reactionId: UUID): Single<Reaction> = remoteDataSource.get(reactionId)

    override fun getForVlog(vlogId: UUID): Single<List<Reaction>> = remoteDataSource.getForVlog(vlogId)

    // TODO This really benefits from caching.
    override fun getCountForVlog(vlogId: UUID): Single<DatasetStats> = remoteDataSource.getCountForVlog(vlogId)

    override fun post(reaction: Reaction): Completable = remoteDataSource.post(reaction)

    override fun update(reaction: Reaction): Completable = remoteDataSource.update(reaction)
}
