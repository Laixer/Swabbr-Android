package com.laixer.swabbr.data.repository

import com.laixer.swabbr.data.interfaces.VlogLikeCacheDataSource
import com.laixer.swabbr.data.interfaces.VlogLikeDataSource
import com.laixer.swabbr.domain.interfaces.VlogLikeRepository
import com.laixer.swabbr.domain.model.VlogLike
import com.laixer.swabbr.domain.model.VlogLikeSummary
import com.laixer.swabbr.domain.model.LikingUserWrapper
import com.laixer.swabbr.domain.types.Pagination
import io.reactivex.Completable
import io.reactivex.Single
import java.util.*

// TODO Use caching
/**
 *  Vlog like repository implementation.
 */
class VlogLikeRepositoryImpl constructor(
    private val cacheDataSource: VlogLikeCacheDataSource,
    private val remoteDataSource: VlogLikeDataSource
) : VlogLikeRepository {
    override fun exists(vlogId: UUID, userId: UUID): Single<Boolean> = remoteDataSource.exists(vlogId, userId)

    override fun get(vlogId: UUID, userId: UUID): Single<VlogLike> = remoteDataSource.get(vlogId, userId)

    // TODO This REALLY benefits from caching
    override fun getVlogLikeSummary(vlogId: UUID): Single<VlogLikeSummary> = remoteDataSource.getVlogLikeSummary(vlogId)

    override fun getLikes(vlogId: UUID, pagination: Pagination): Single<List<VlogLike>> =
        remoteDataSource.getLikes(vlogId, pagination)

    override fun like(vlogId: UUID): Completable = remoteDataSource.like(vlogId)

    override fun unlike(vlogId: UUID): Completable = remoteDataSource.unlike(vlogId)
}
