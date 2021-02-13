package com.laixer.swabbr.domain.interfaces

import com.laixer.swabbr.domain.model.VlogLike
import com.laixer.swabbr.domain.model.VlogLikeSummary
import com.laixer.swabbr.domain.types.Pagination
import io.reactivex.Completable
import io.reactivex.Single
import java.util.*

/**
 *  Interface for a vlog repository.
 */
interface VlogLikeRepository {
    /**
     *  Checks if a vlog like which would represent [userId]
     *  liking vlog [vlogId].
     *
     *  @param vlogId The vlog that would be liked.
     *  @param userId The user that would like the vlog.
     */
    fun exists(vlogId: UUID, userId: UUID): Single<Boolean>

    /**
     *  Gets a vlog like which represents [userId] liking
     *  vlog [vlogId].
     *
     *  @param vlogId The vlog which is liked.
     *  @param userId The user which liked the vlog.
     */
    fun get(vlogId: UUID, userId: UUID): Single<VlogLike>

    /**
     *  Gets a summary of vlog likes for a vlog.
     *
     *  @param vlogId The vlog to summarize.
     */
    fun getVlogLikeSummary(vlogId: UUID): Single<VlogLikeSummary>

    /**
     *  Gets a collection of likes for a vlog.
     *
     *  @param vlogId The vlog to get likes for.
     *  @param pagination Controls the result set.
     */
    fun getLikes(vlogId: UUID, pagination: Pagination): Single<List<VlogLike>>

    /**
     *  Likes a vlog as the current user.
     *
     *  @param vlogId The vlog to like.
     */
    fun like(vlogId: UUID): Completable

    /**
     *  Unlikes a vlog as the current user.
     *
     *  @param vlogId The vlog to unlike.
     */
    fun unlike(vlogId: UUID): Completable
}
