package com.laixer.swabbr.data.interfaces

import com.laixer.swabbr.domain.model.VlogLike
import com.laixer.swabbr.domain.model.VlogLikeSummary
import com.laixer.swabbr.domain.model.LikingUserWrapper
import com.laixer.swabbr.domain.types.Pagination
import io.reactivex.Completable
import io.reactivex.Single
import java.util.*

/**
 * Interface for caching vlog like data.
 */
interface VlogLikeCacheDataSource {
    // TODO Implement
}

/**
 * Interface for a vlog like data source.
 */
interface VlogLikeDataSource {
    fun exists(vlogId: UUID, userId: UUID): Single<Boolean>

    fun get(vlogId: UUID, userId: UUID): Single<VlogLike>

    fun getVlogLikeSummary(vlogId: UUID): Single<VlogLikeSummary>

    fun getLikes(vlogId: UUID, pagination: Pagination): Single<List<VlogLike>>

    fun like(vlogId: UUID): Completable

    fun unlike(vlogId: UUID): Completable
}
