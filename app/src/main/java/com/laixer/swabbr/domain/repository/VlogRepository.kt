package com.laixer.swabbr.domain.repository

import com.laixer.swabbr.domain.model.UploadWrapper
import com.laixer.swabbr.domain.model.Vlog
import com.laixer.swabbr.domain.model.VlogLike
import com.laixer.swabbr.domain.model.VlogLikeSummary
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

    fun getRecommended(): Single<List<Vlog>>

    fun getForUser(userId: UUID): Single<List<Vlog>>

    fun like(vlogId: UUID): Completable

    fun post(vlog: Vlog): Completable

    fun unlike(vlogId: UUID): Completable

    fun update(vlog: Vlog): Completable
}
