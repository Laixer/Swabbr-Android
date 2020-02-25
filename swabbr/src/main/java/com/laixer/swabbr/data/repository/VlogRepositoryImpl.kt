package com.laixer.swabbr.data.repository

import com.laixer.swabbr.data.datasource.VlogCacheDataSource
import com.laixer.swabbr.data.datasource.VlogRemoteDataSource
import com.laixer.swabbr.domain.model.Vlog
import com.laixer.swabbr.domain.repository.VlogRepository
import io.reactivex.Single

class VlogRepositoryImpl constructor(
    private val cacheDataSource: VlogCacheDataSource,
    private val remoteDataSource: VlogRemoteDataSource
) : VlogRepository {

    override fun getUserVlogs(userId: String, refresh: Boolean): Single<List<Vlog>> =
        when (refresh) {
            true -> remoteDataSource.getUserVlogs(userId).flatMap { cacheDataSource.set(it) }
            false -> cacheDataSource.getUserVlogs(userId).onErrorResumeNext { getUserVlogs(userId, true) }
        }

    override fun get(vlogId: String, refresh: Boolean): Single<Vlog> =
        when (refresh) {
            true -> remoteDataSource.get(vlogId).flatMap { cacheDataSource.set(it) }
            false -> cacheDataSource.get(vlogId).onErrorResumeNext { get(vlogId, true) }
        }

    override fun getFeaturedVlogs(refresh: Boolean): Single<List<Vlog>> =
        when (refresh) {
            true -> remoteDataSource.getFeaturedVlogs().flatMap { cacheDataSource.setFeaturedVlogs(it) }
            false -> cacheDataSource.getFeaturedVlogs().onErrorResumeNext { getFeaturedVlogs(true) }
        }
}
