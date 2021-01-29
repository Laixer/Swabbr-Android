package com.laixer.swabbr.domain.usecase

import com.laixer.swabbr.domain.model.Reaction
import com.laixer.swabbr.domain.model.UploadWrapper
import com.laixer.swabbr.domain.model.Vlog
import com.laixer.swabbr.domain.model.VlogLikeSummary
import com.laixer.swabbr.domain.repository.ReactionRepository
import com.laixer.swabbr.domain.repository.UserRepository
import com.laixer.swabbr.domain.repository.VlogRepository
import com.laixer.swabbr.domain.types.VlogWrapper
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import java.util.*

// TODO Performance could be gained here when adding the user and vloglikesummary to one or multiple vlogs.
// TODO Pass refresh everywhere
/**
 *  Use case which handles vlog related requests. This bundles vlogs
 *  with the user that owns the vlog as a [Pair].
 *
 *  Note: This does NOT handle vlog recording, @see TODO
 */
class VlogUseCase constructor(
    private val userRepository: UserRepository,
    private val vlogRepository: VlogRepository,
    private val reactionRepository: ReactionRepository
) {
    /**
     *  Gets all recommended vlogs for the currently authenticated
     *  user. The result is returned as vlog wrapper objects.
     *
     *  @param refresh Force a refresh of the data.
     */
    fun getRecommendedVlogs(refresh: Boolean): Single<List<VlogWrapper>> = vlogRepository
        .getRecommended()
        .toCompleteVlogWrapper(refresh)
        .toList()

    /**
     *  For a list of vlog ids, return those vlogs paired with the
     *  user who posted them.
     *
     *  @param idList All vlog ids to retrieve.
     *  @param refresh Force a refresh of the data.
     */
    fun getFromIdList(idList: List<UUID>, refresh: Boolean): Single<List<VlogWrapper>> = Observable
        .just(idList)
        .flatMapIterable { ids -> ids }
        .flatMapSingle { id -> vlogRepository.get(id) }
        .toCompleteVlogWrapper(refresh)
        .toList()

    /**
     *  Get a vlog paired with the user who posted it.
     *
     *  @param vlogId The vlog to get.
     *  @return Force a refresh of the data.
     */
    fun get(vlogId: UUID, refresh: Boolean): Single<VlogWrapper> = vlogRepository
        .get(vlogId)
        .toCompleteVlogWrapper(refresh)

    /**
     *  Get all vlogs that belong with a given user and return each
     *  paired with said user.
     *
     *  @param userId The user that owns the vlogs.
     *  @param refresh Force a refresh of the data.
     */
    fun getAllForUser(userId: UUID, refresh: Boolean): Single<List<VlogWrapper>> = vlogRepository
        .getForUser(userId)
        .toCompleteVlogWrapper(refresh)
        .toList()

    /**
     *  Delete a vlog which is owned by the currently authenticated user.
     *
     *  @param vlogId The vlog to delete.
     */
    fun delete(vlogId: UUID): Completable = vlogRepository.delete(vlogId)

    // FUTURE: Use dataset stats object to handle these kind of set statistics.
    // TODO Backend This can also become part of a summary of some kind.
    // TODO Shouldn't this be in @see ReactionUseCase?
    /**
     *  Get the total amount of reactions for a vlog.
     *
     * @param vlogId The vlog id.
     */
    fun getReactionCount(vlogId: UUID): Single<Int> = reactionRepository.getCountForVlog(vlogId).map { it.count }

    /**
     *  Gets a vlog like summary for a vlog.
     *
     *  @param Vlog id to get a summary for.
     */
    fun getVlogLikeSummary(vlogId: UUID): Single<VlogLikeSummary> =
        vlogRepository.getVlogLikeSummary(vlogId)

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
    fun like(vlogId: UUID): Completable = vlogRepository.like(vlogId)

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
    fun unlike(vlogId: UUID): Completable = vlogRepository.unlike(vlogId)

    /**
     *  Extension functionality to convert an observable vlog
     *  to a [VlogWrapper].
     *
     *  @param refresh Force a data refresh.
     */
    private fun Observable<Vlog>.toCompleteVlogWrapper(refresh: Boolean): Observable<VlogWrapper> = this
        .flatMapSingle { vlog ->
            userRepository
                .get(vlog.userId, refresh)
                .map { user -> Pair(user, vlog) }
        }
        .flatMapSingle { pair ->
            vlogRepository
                .getVlogLikeSummary(pair.second.id)
                .map { summary ->
                    VlogWrapper(
                        pair.first,
                        pair.second,
                        summary
                    )
                }
        }

    /**
     *  Extension functionality to convert a list of vlogs
     *  to [VlogWrapper] objects.
     *
     *  @param refresh Force a data refresh.
     */
    private fun Single<List<Vlog>>.toCompleteVlogWrapper(refresh: Boolean): Observable<VlogWrapper> = this
        .flattenAsObservable { vlogs -> vlogs }
        .toCompleteVlogWrapper(refresh)

    // TODO Duplicate code?
    /**
     *  Extension functionality to convert a single vlog to
     *  a [VlogWrapper].
     *
     *  @param refresh Force a data refresh.
     */
    private fun Single<Vlog>.toCompleteVlogWrapper(refresh: Boolean): Single<VlogWrapper> = this
        .flatMap { vlog ->
            userRepository
                .get(vlog.userId, refresh)
                .map { user -> Pair(user, vlog) }
        }
        .flatMap { pair ->
            vlogRepository
                .getVlogLikeSummary(pair.second.id)
                .map { summary ->
                    VlogWrapper(
                        pair.first,
                        pair.second,
                        summary
                    )
                }
        }
}
