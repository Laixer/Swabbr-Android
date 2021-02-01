package com.laixer.swabbr.domain.repository

import com.laixer.swabbr.domain.model.UploadWrapper
import com.laixer.swabbr.domain.model.Vlog
import com.laixer.swabbr.domain.model.VlogLike
import com.laixer.swabbr.domain.model.VlogLikeSummary
import com.laixer.swabbr.domain.types.Pagination
import io.reactivex.Completable
import io.reactivex.Single
import java.util.*

/**
 *  Interface for a vlog repository.
 */
interface VlogRepository {

    // TODO Implement
    fun addView(vlogId: UUID): Completable

    fun delete(vlogId: UUID): Completable

    fun get(vlogId: UUID): Single<Vlog>

    fun generateUploadWrapper(): Single<UploadWrapper>

    fun getVlogLikeSummary(vlogId: UUID): Single<VlogLikeSummary>

    fun getLikes(vlogId: UUID): Single<List<VlogLike>>

    fun getRecommended(pagination: Pagination = Pagination.latest()): Single<List<Vlog>>

    fun getForUser(userId: UUID, pagination: Pagination = Pagination.latest()): Single<List<Vlog>>

    fun like(vlogId: UUID): Completable

    fun post(vlog: Vlog): Completable

    fun unlike(vlogId: UUID): Completable

    fun update(vlog: Vlog): Completable
}
