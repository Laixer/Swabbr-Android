package com.laixer.swabbr.data.datasource.remote

import com.laixer.swabbr.data.datasource.VlogDataSource
import com.laixer.swabbr.data.datasource.model.mapToData
import com.laixer.swabbr.data.datasource.model.mapToDomain
import com.laixer.swabbr.data.datasource.model.remote.VlogApi
import com.laixer.swabbr.domain.model.UploadWrapper
import com.laixer.swabbr.domain.model.Vlog
import com.laixer.swabbr.domain.model.VlogLike
import com.laixer.swabbr.domain.model.VlogLikeSummary
import io.reactivex.Completable
import io.reactivex.Single
import java.util.*

class VlogDataSourceImpl constructor(
    val api: VlogApi
) : VlogDataSource {

    override fun addView(vlogId: UUID): Completable {
        TODO("Not yet implemented")
    }

    override fun delete(vlogId: UUID): Completable = api.delete(vlogId)

    override fun generateUploadWrapper(): Single<UploadWrapper> = api.generateUploadWrapper().map { it.mapToDomain() }

    override fun get(vlogId: UUID): Single<Vlog> = api.getVlog(vlogId).map { it.mapToDomain() }

    override fun getVlogLikeSummary(vlogId: UUID): Single<VlogLikeSummary> =
        api.getVlogLikeSummary(vlogId).map { it.mapToDomain() }

    override fun getLikes(vlogId: UUID): Single<List<VlogLike>> = api.getLikes(vlogId).map { it.mapToDomain() }

    override fun getRecommended(): Single<List<Vlog>> = api.getRecommendedVlogs().map { it.mapToDomain() }

    override fun getForUser(userId: UUID): Single<List<Vlog>> = api.getVlogsForUser(userId).map { it.mapToDomain() }

    override fun like(vlogId: UUID): Completable = api.like(vlogId)

    override fun post(vlog: Vlog): Completable = api.postVlog(vlog.mapToData())

    override fun unlike(vlogId: UUID): Completable = api.unlike(vlogId)

    override fun update(updatedVlog: Vlog): Completable = api.updateVlog(updatedVlog.id, updatedVlog.mapToData())
}
