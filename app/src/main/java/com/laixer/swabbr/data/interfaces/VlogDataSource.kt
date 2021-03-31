package com.laixer.swabbr.data.interfaces

import com.laixer.swabbr.domain.model.UploadWrapper
import com.laixer.swabbr.domain.model.Vlog
import com.laixer.swabbr.domain.model.VlogViews
import com.laixer.swabbr.domain.model.VlogWrapper
import com.laixer.swabbr.domain.types.Pagination
import io.reactivex.Completable
import io.reactivex.Single
import java.util.*

/**
 * Interface for caching vlog data.
 */
interface VlogCacheDataSource {
    val key: String get() = "VLOGS"
    val keyRecommended: String get() = "RECOMMENDED_VLOGS"

    fun getForUser(userId: UUID): Single<List<Vlog>>

    fun set(list: List<Vlog>): Single<List<Vlog>>

    fun get(vlogId: UUID): Single<Vlog>

    fun set(item: Vlog): Single<Vlog>

    fun getRecommendedVlogs(): Single<List<Vlog>>

    fun setRecommendedVlogs(list: List<Vlog>): Single<List<Vlog>>

    fun delete(vlogId: UUID): Completable
}

/**
 * Interface for a vlog data source.
 */
interface VlogDataSource {
    fun addViews(vlogViews: VlogViews): Completable

    fun delete(vlogId: UUID): Completable

    fun generateUploadWrapper(): Single<UploadWrapper>

    fun get(vlogId: UUID): Single<Vlog>

    fun getWrapper(vlogId: UUID): Single<VlogWrapper>

    fun getRecommended(pagination: Pagination = Pagination.latest()): Single<List<Vlog>>

    fun getWrappersRecommended(pagination: Pagination = Pagination.latest()): Single<List<VlogWrapper>>

    fun getForUser(userId: UUID, pagination: Pagination = Pagination.latest()): Single<List<Vlog>>

    fun getWrappersForUser(userId: UUID, pagination: Pagination = Pagination.latest()): Single<List<VlogWrapper>>

    fun post(vlog: Vlog): Completable

    fun update(updatedVlog: Vlog): Completable
}
