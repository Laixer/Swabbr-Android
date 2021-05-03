package com.laixer.swabbr.data.datasource

import com.laixer.swabbr.data.api.VlogApi
import com.laixer.swabbr.data.interfaces.VlogDataSource
import com.laixer.swabbr.data.model.mapToData
import com.laixer.swabbr.data.model.mapToDomain
import com.laixer.swabbr.domain.model.UploadWrapper
import com.laixer.swabbr.domain.model.Vlog
import com.laixer.swabbr.domain.model.VlogViews
import com.laixer.swabbr.domain.model.VlogWrapper
import com.laixer.swabbr.domain.types.Pagination
import io.reactivex.Completable
import io.reactivex.Single
import java.util.*

/**
 *  Data source implementation for vlog entities.
 */
class VlogDataSourceImpl constructor(
    val api: VlogApi
) : VlogDataSource {
    override fun addViews(vlogViews: VlogViews): Completable = api.addViews(vlogViews.mapToData())

    override fun delete(vlogId: UUID): Completable = api.delete(vlogId)

    override fun generateUploadWrapper(): Single<UploadWrapper> = api.generateUploadWrapper().map { it.mapToDomain() }

    override fun get(vlogId: UUID): Single<Vlog> = api.getVlog(vlogId).map { it.mapToDomain() }

    override fun getWrapper(vlogId: UUID): Single<VlogWrapper> = api.getVlogWrapper(vlogId).map { it.mapToDomain() }

    override fun getRecommended(pagination: Pagination): Single<List<Vlog>> =
        api.getRecommendedVlogs(pagination.sortingOrder.ordinal, pagination.limit, pagination.offset)
            .map { it.mapToDomain() }

    override fun getWrappersRecommended(pagination: Pagination): Single<List<VlogWrapper>> =
        api.getRecommendedVlogWrappers(pagination.sortingOrder.ordinal, pagination.limit, pagination.offset)
            .map { it.mapToDomain() }

    override fun getForUser(userId: UUID, pagination: Pagination): Single<List<Vlog>> =
        api.getVlogsForUser(userId, pagination.sortingOrder.ordinal, pagination.limit, pagination.offset)
            .map { it.mapToDomain() }

    override fun getWrappersForUser(userId: UUID, pagination: Pagination): Single<List<VlogWrapper>> =
        api.getVlogWrappersForUser(userId, pagination.sortingOrder.ordinal, pagination.limit, pagination.offset)
            .map { it.mapToDomain() }

    override fun post(vlog: Vlog): Completable = api.postVlog(vlog.mapToData())

    override fun update(updatedVlog: Vlog): Completable = api.updateVlog(updatedVlog.id, updatedVlog.mapToData())
}
