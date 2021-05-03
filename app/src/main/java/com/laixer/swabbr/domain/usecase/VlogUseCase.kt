package com.laixer.swabbr.domain.usecase

import com.laixer.swabbr.domain.interfaces.ReactionRepository
import com.laixer.swabbr.domain.interfaces.UserRepository
import com.laixer.swabbr.domain.interfaces.VlogLikeRepository
import com.laixer.swabbr.domain.interfaces.VlogRepository
import com.laixer.swabbr.domain.model.*
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import java.util.*

// TODO Pass refresh everywhere
/**
 *  Use case which handles vlog related requests. This bundles vlogs
 *  with the user that owns the vlog as a [Pair].
 */
class VlogUseCase constructor(
    private val vlogRepository: VlogRepository,
    private val vlogLikeRepository: VlogLikeRepository,
    private val reactionRepository: ReactionRepository
) {
    // FUTURE Do this smarter, the backend allows us to do so.
    /**
     *  Adds a single view to the vlog with id [vlogId].
     *
     *  @param vlogId The vlog we watched.
     */
    fun addView(vlogId: UUID): Completable = vlogRepository.addView(VlogViews(vlogId))

    /**
     *  Gets all recommended vlogs for the currently authenticated
     *  user. The result is returned as vlog wrapper objects.
     *
     *  @param refresh Force a refresh of the data.
     */
    fun getRecommendedVlogs(refresh: Boolean): Single<List<VlogWrapper>> = vlogRepository.getWrappersRecommended()

    /**
     *  Get a vlog paired with the user who posted it.
     *
     *  @param vlogId The vlog to get.
     *  @return Force a refresh of the data.
     */
    fun get(vlogId: UUID, refresh: Boolean): Single<VlogWrapper> = vlogRepository.getWrapper(vlogId)

    /**
     *  Get all vlogs that belong with a given user and return each
     *  paired with said user.
     *
     *  @param userId The user that owns the vlogs.
     *  @param refresh Force a refresh of the data.
     */
    fun getAllForUser(userId: UUID, refresh: Boolean): Single<List<VlogWrapper>> =
        vlogRepository.getWrappersForUser(userId)

    /**
     *  Delete a vlog which is owned by the currently authenticated user.
     *
     *  @param vlogId The vlog to delete.
     */
    fun delete(vlogId: UUID): Completable = vlogRepository.delete(vlogId)

    // FUTURE: Use dataset stats object to handle these kind of set statistics.
    /**
     *  Get the total amount of reactions for a vlog.
     *
     * @param vlogId The vlog id.
     * @param refresh Force a data refresh.
     */
    fun getReactionCount(vlogId: UUID, refresh: Boolean = false): Single<Int> =
        reactionRepository.getCountForVlog(vlogId).map { it.count }

    /**
     *  Checks if a vlog is liked by a user.
     *
     *  @param vlogId The vlog.
     *  @param userId The user.
     */
    fun isVlogLikedByUser(vlogId: UUID, userId: UUID): Single<Boolean> =
        vlogLikeRepository.exists(vlogId, userId)

    /**
     *  Generates a new upload wrapper for a vlog.
     *
     *  @return Wrapper also containing the id of the vlog.
     */
    fun generateUploadWrapper(): Single<UploadWrapper> = vlogRepository.generateUploadWrapper()

    /**
     *  Like a given vlog as the authenticated user.
     *
     *  @param vlogId The vlog to like.
     */
    fun like(vlogId: UUID): Completable = vlogLikeRepository.like(vlogId)

    /**
     *  Posts a new vlog to the backend. The vlog should already
     *  be uploaded to the blob storage along with its thumbnail
     *  when calling this function.
     *
     *  @param vlog The vlog we wish to post.
     */
    fun postVlog(vlog: Vlog): Completable = vlogRepository.post(vlog)

    /**
     *  Unlike a given vlog as the authenticated user.
     *
     *  @param vlogId The vlog to like.
     */
    fun unlike(vlogId: UUID): Completable = vlogLikeRepository.unlike(vlogId)
}
