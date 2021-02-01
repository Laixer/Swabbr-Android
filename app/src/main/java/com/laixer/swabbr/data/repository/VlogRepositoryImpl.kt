package com.laixer.swabbr.data.repository

import com.laixer.swabbr.data.datasource.VlogCacheDataSource
import com.laixer.swabbr.data.datasource.VlogDataSource
import com.laixer.swabbr.domain.model.UploadWrapper
import com.laixer.swabbr.domain.model.Vlog
import com.laixer.swabbr.domain.model.VlogLike
import com.laixer.swabbr.domain.model.VlogLikeSummary
import com.laixer.swabbr.domain.repository.VlogRepository
import com.laixer.swabbr.domain.types.Pagination
import io.reactivex.Completable
import io.reactivex.Single
import java.util.*

// TODO Use caching refresh option.
/**
 *  Vlog repository implementation.
 */
class VlogRepositoryImpl constructor(
    private val cacheDataSource: VlogCacheDataSource,
    private val remoteDataSource: VlogDataSource
) : VlogRepository {

    override fun addView(vlogId: UUID): Completable {
        TODO("Not yet implemented")
    }

    override fun delete(vlogId: UUID): Completable =
        remoteDataSource.delete(vlogId).doOnComplete { cacheDataSource.delete(vlogId) }

    override fun generateUploadWrapper(): Single<UploadWrapper> = remoteDataSource.generateUploadWrapper()

    override fun get(vlogId: UUID): Single<Vlog> = remoteDataSource.get(vlogId)

    // TODO This REALLY benefits from caching
    override fun getVlogLikeSummary(vlogId: UUID): Single<VlogLikeSummary> = remoteDataSource.getVlogLikeSummary(vlogId)

    override fun getLikes(vlogId: UUID): Single<List<VlogLike>> = remoteDataSource.getLikes(vlogId)

    override fun getRecommended(pagination: Pagination): Single<List<Vlog>> =
        remoteDataSource.getRecommended(pagination)

    override fun getForUser(userId: UUID, pagination: Pagination): Single<List<Vlog>> =
        remoteDataSource.getForUser(userId, pagination)

    override fun like(vlogId: UUID): Completable = remoteDataSource.like(vlogId)

    override fun post(vlog: Vlog): Completable = remoteDataSource.post(vlog)

    override fun unlike(vlogId: UUID): Completable = remoteDataSource.unlike(vlogId)

    override fun update(vlog: Vlog): Completable = remoteDataSource.update(vlog)
}
