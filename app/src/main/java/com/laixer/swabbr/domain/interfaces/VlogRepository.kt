package com.laixer.swabbr.domain.interfaces

import com.laixer.swabbr.domain.model.*
import com.laixer.swabbr.domain.types.Pagination
import io.reactivex.Completable
import io.reactivex.Single
import java.util.*

/**
 *  Interface for a vlog repository.
 */
interface VlogRepository {
    fun addView(vlogViews: VlogViews): Completable

    fun delete(vlogId: UUID): Completable

    fun get(vlogId: UUID): Single<Vlog>

    fun getWrapper(vlogId: UUID): Single<VlogWrapper>

    fun generateUploadWrapper(): Single<UploadWrapper>

    fun getRecommended(pagination: Pagination = Pagination.latest()): Single<List<Vlog>>

    fun getWrappersRecommended(pagination: Pagination = Pagination.latest()): Single<List<VlogWrapper>>

    fun getForUser(userId: UUID, pagination: Pagination = Pagination.latest()): Single<List<Vlog>>

    fun getWrappersForUser(userId: UUID, pagination: Pagination = Pagination.latest()): Single<List<VlogWrapper>>

    fun post(vlog: Vlog): Completable

    fun update(vlog: Vlog): Completable
}
