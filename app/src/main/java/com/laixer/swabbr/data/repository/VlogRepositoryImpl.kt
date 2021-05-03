package com.laixer.swabbr.data.repository

import com.laixer.swabbr.data.interfaces.VlogCacheDataSource
import com.laixer.swabbr.data.interfaces.VlogDataSource
import com.laixer.swabbr.domain.interfaces.VlogRepository
import com.laixer.swabbr.domain.model.UploadWrapper
import com.laixer.swabbr.domain.model.Vlog
import com.laixer.swabbr.domain.model.VlogViews
import com.laixer.swabbr.domain.model.VlogWrapper
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
    override fun addView(vlogViews: VlogViews): Completable = remoteDataSource.addViews(vlogViews)

    override fun delete(vlogId: UUID): Completable =
        remoteDataSource.delete(vlogId).doOnComplete { cacheDataSource.delete(vlogId) }

    override fun generateUploadWrapper(): Single<UploadWrapper> = remoteDataSource.generateUploadWrapper()

    override fun get(vlogId: UUID): Single<Vlog> = remoteDataSource.get(vlogId)

    override fun getWrapper(vlogId: UUID): Single<VlogWrapper> = remoteDataSource.getWrapper(vlogId)

    override fun getRecommended(pagination: Pagination): Single<List<Vlog>> =
        remoteDataSource.getRecommended(pagination)

    override fun getWrappersRecommended(pagination: Pagination): Single<List<VlogWrapper>> =
        remoteDataSource.getWrappersRecommended(pagination)

    override fun getForUser(userId: UUID, pagination: Pagination): Single<List<Vlog>> =
        remoteDataSource.getForUser(userId, pagination)

    override fun getWrappersForUser(userId: UUID, pagination: Pagination): Single<List<VlogWrapper>> =
        remoteDataSource.getWrappersForUser(userId, pagination)

    override fun post(vlog: Vlog): Completable = remoteDataSource.post(vlog)

    override fun update(vlog: Vlog): Completable = remoteDataSource.update(vlog)
}
