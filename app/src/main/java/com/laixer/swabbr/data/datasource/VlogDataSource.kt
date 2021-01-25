package com.laixer.swabbr.data.datasource

import com.laixer.swabbr.domain.model.UploadWrapper
import com.laixer.swabbr.domain.model.Vlog
import com.laixer.swabbr.domain.model.VlogLike
import com.laixer.swabbr.domain.model.VlogLikeSummary
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

    // TODO Implement
    fun addView(vlogId: UUID): Completable

    fun delete(vlogId: UUID): Completable

    fun generateUploadWrapper(): Single<UploadWrapper>

    fun get(vlogId: UUID): Single<Vlog>

    fun getVlogLikeSummary(vlogId: UUID): Single<VlogLikeSummary>

    fun getLikes(vlogId: UUID): Single<List<VlogLike>>

    fun getRecommended(): Single<List<Vlog>>

    fun getForUser(userId: UUID): Single<List<Vlog>>

    fun like(vlogId: UUID): Completable

    fun post(vlog: Vlog): Completable

    fun unlike(vlogId: UUID): Completable

    fun update(updatedVlog: Vlog): Completable
}
