package com.laixer.sample.datasource.remote

import com.laixer.sample.data.datasource.VlogRemoteDataSource
import com.laixer.sample.datasource.model.mapToDomain
import com.laixer.sample.domain.model.Vlog
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
}
