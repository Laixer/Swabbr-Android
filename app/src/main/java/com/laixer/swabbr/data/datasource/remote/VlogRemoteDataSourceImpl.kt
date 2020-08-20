package com.laixer.swabbr.data.datasource.remote

import com.laixer.swabbr.data.datasource.VlogRemoteDataSource
import com.laixer.swabbr.data.datasource.model.WatchVlogResponse
import com.laixer.swabbr.data.datasource.model.mapToDomain
import com.laixer.swabbr.data.datasource.model.remote.VlogsApi
import com.laixer.swabbr.domain.model.LikeList
import com.laixer.swabbr.domain.model.Vlog
import io.reactivex.Completable
import io.reactivex.Single
import java.util.UUID

class VlogRemoteDataSourceImpl constructor(
    val api: VlogsApi
) : VlogRemoteDataSource {

    override fun getUserVlogs(userId: UUID): Single<List<Vlog>> = api.getUserVlogs(userId)
            .map { it.vlogs.mapToDomain() }

    override fun get(vlogId: UUID): Single<Vlog> = api.getVlog(vlogId)
        .map { it.vlog.mapToDomain() }

    override fun getRecommendedVlogs(): Single<List<Vlog>> = api.getRecommendedVlogs()
        .map { it.vlogs.mapToDomain () }

    override fun getLikes(vlogId: UUID): Single<LikeList> = api.getLikes(vlogId)
        .map { it.mapToDomain() }

    override fun like(vlogId: UUID): Completable = api.like(vlogId)

    override fun unlike(vlogId: UUID): Completable = api.unlike(vlogId)

    override fun watch(vlogId: UUID): Single<WatchVlogResponse> = api.watch(vlogId)
}
