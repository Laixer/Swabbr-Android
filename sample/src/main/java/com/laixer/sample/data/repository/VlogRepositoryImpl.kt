package com.laixer.sample.data.repository

import com.laixer.sample.data.datasource.VlogCacheDataSource
import com.laixer.sample.data.datasource.VlogRemoteDataSource
import com.laixer.sample.domain.model.Vlog
import com.laixer.sample.domain.repository.VlogRepository
import io.reactivex.Single

class VlogRepositoryImpl constructor(
    private val cacheDataSource: VlogCacheDataSource,
    private val remoteDataSource: VlogRemoteDataSource
) : VlogRepository {

    override fun get(refresh: Boolean): Single<List<Vlog>> =
        when (refresh) {
            true -> remoteDataSource.get()
                .flatMap { cacheDataSource.set(it) }
            false -> cacheDataSource.get()
                .onErrorResumeNext { get(true) }
        }

    override fun get(vlogId: String, refresh: Boolean): Single<Vlog> =
        when (refresh) {
            true -> remoteDataSource.get(vlogId)
                .flatMap { cacheDataSource.set(it) }
            false -> cacheDataSource.get(vlogId)
                .onErrorResumeNext { get(vlogId, true) }
        }
}
