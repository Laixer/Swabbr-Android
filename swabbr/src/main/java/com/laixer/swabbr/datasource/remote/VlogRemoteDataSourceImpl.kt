package com.laixer.swabbr.datasource.remote

import com.laixer.swabbr.data.datasource.VlogRemoteDataSource
import com.laixer.swabbr.datasource.model.mapToDomain
import com.laixer.swabbr.domain.model.Vlog
import io.reactivex.Single

class VlogRemoteDataSourceImpl constructor(
    private val api: VlogsApi
) : VlogRemoteDataSource {

    override fun get(): Single<List<Vlog>> =
        api.getVlogs()
            .map { it.mapToDomain() }

    override fun get(vlogId: String): Single<Vlog> =
        api.getVlog(vlogId)
            .map { it.mapToDomain() }

    override fun getFeaturedVlogs(): Single<List<Vlog>> =
        api.getFeaturedVlogs()
            .map { it.mapToDomain() }
}
