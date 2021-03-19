package com.laixer.swabbr.presentation.vlogs.playback

import androidx.lifecycle.MutableLiveData
import com.laixer.swabbr.utils.resources.Resource
import com.laixer.swabbr.presentation.utils.todosortme.setError
import com.laixer.swabbr.presentation.utils.todosortme.setLoading
import com.laixer.swabbr.presentation.utils.todosortme.setSuccess
import com.laixer.swabbr.domain.usecase.AuthUserUseCase
import com.laixer.swabbr.domain.usecase.ReactionUseCase
import com.laixer.swabbr.domain.usecase.VlogUseCase
import com.laixer.swabbr.presentation.model.ReactionItem
import com.laixer.swabbr.presentation.model.ReactionWrapperItem
import com.laixer.swabbr.presentation.model.VlogWrapperItem
import com.laixer.swabbr.presentation.model.mapToPresentation
import com.laixer.swabbr.presentation.abstraction.ViewModelBase
import io.reactivex.schedulers.Schedulers
import java.util.*

/**
 *  View model which contains details about a single vlog. If
 *  a list of vlogs is desired, use [VlogListViewModel]. This
 *  also contains functionality for the reactions that belong
 *  to the [vlog] resource. Watching vlogs is done using the
 *  [ReactionViewModel].
 */
class VlogViewModel constructor(
    private val authUserUseCase: AuthUserUseCase,
    private val reactionsUseCase: ReactionUseCase,
    private val vlogUseCase: VlogUseCase
) : ViewModelBase() {
    /**
     *  Used to store a vlog when we watch a single item.
     */
    val vlog = MutableLiveData<Resource<VlogWrapperItem>>()

    /**
     *  Used to store reactions for the [vlog] resource.
     */
    val reactions = MutableLiveData<Resource<List<ReactionWrapperItem>>>()

    /**
     *  Used to store the amount of reactions for the [vlog] resource.
     */
    val reactionCount = MutableLiveData<Resource<Int>>()

    /**
     *  Used to store the amount of likes for the [vlog] resource.
     *  Not that this also gets updated after we get the [vlog]
     *  itself.
     */
    val vlogLikeCount = MutableLiveData<Resource<Int>>()

    /**
     *  Used to store whether or not the current user has
     *  liked the [vlog] resource.
     */
    val vlogLikedByCurrentUser = MutableLiveData<Resource<Boolean>>()

    /**
     *  Adds a single vlog view to a vlog.
     *
     *  @param vlogId The vlog we watched.
     */
    fun addView(vlogId: UUID) = compositeDisposable.add(
        vlogUseCase.addView(vlogId)
            .subscribeOn(Schedulers.io())
            .subscribe({}, {}) // We always want an error handler even if it's empty.
    )

    // TODO Is this the way to go? I do think so.
    /**
     *  Sets all the single vlog resources to the loading state.
     */
    fun clearResources() {
        vlog.setLoading()
        reactions.setLoading()
        reactionCount.setLoading()
        vlogLikeCount.setLoading()
        vlogLikedByCurrentUser.setLoading()
    }

    /**
     *  Deletes a reaction. Note that this will also remove
     *  the reaction from the [reactions] resource. This will
     *  only proceed if we own the reaction.
     */
    fun deleteReaction(reaction: ReactionItem) {
        if (reaction.userId != authUserUseCase.getSelfId()) {
            return
        }

        // First remove the reaction locally.
        reactions.value?.data?.let { list ->
            reactions.setSuccess(list.filter { it.reaction.id != reaction.id })
        }

        // Then call the API
        compositeDisposable.add(
            reactionsUseCase.deleteReaction(reaction.id)
                .subscribeOn(Schedulers.io())
                .subscribe({}, {}) // We always want an error handler even if it's empty.
        )
    }

    /**
     *  Gets a vlog and stores it in [vlogs]. Also store the
     *  vlog like count in [vlogLikeCount] based on the summary
     *  contained in the [VlogWrapperItem].
     *
     *  @param vlogId The vlog to get.
     *  @param refresh Force a data refresh.
     */
    fun getVlog(vlogId: UUID, refresh: Boolean = false) =
        compositeDisposable.add(
            vlogUseCase.get(vlogId, refresh)
                .doOnSubscribe { vlog.setLoading() }
                .subscribeOn(Schedulers.io()).map { it.mapToPresentation() }
                .subscribe(
                    {
                        vlog.setSuccess(it)
                        vlogLikeCount.setSuccess(it.vlogLikeSummary.totalLikes)
                    },
                    { vlog.setError(it.message) }
                )
        )

    /**
     *  Get reaction for a vlog and store them in [reactions].
     *  Note that the reactions are expected to belong to [vlog].
     *
     *  @param vlogId The vlog to get reactions for.
     *  @param refresh Force a data refresh, false by default.
     */
    fun getReactions(vlogId: UUID, refresh: Boolean = false) =
        compositeDisposable.add(
            reactionsUseCase.getAllForVlog(vlogId, refresh)
                .doOnSubscribe { reactions.setLoading() }
                .subscribeOn(Schedulers.io())
                .map { it.mapToPresentation() }
                .subscribe(
                    { reactions.setSuccess(it) },
                    { reactions.setError(it.message) }
                )
        )

    /**
     *  Get the amount of reactions that belong to a vlog.
     *
     *  @param vlogId The vlog to get the count for.
     */
    fun getReactionCount(vlogId: UUID, refresh: Boolean = false) =
        compositeDisposable.add(
            vlogUseCase.getReactionCount(vlogId, refresh)
                .doOnSubscribe { reactions.setLoading() }
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { reactionCount.setSuccess(it) },
                    { reactionCount.setError(it.message) }
                )
        )

    /**
     *  Like a vlog as the current user.
     *
     *  Note that this also increases the [vlogLikeCount] by one
     *  and modifies the [vlogLikedByCurrentUser] resource. This
     *  is both done locally, not using the backend.
     *
     *  @param vlogId The vlog to like.
     */
    fun like(vlogId: UUID) {
        // First update locally
        modifyVlogLikeCount(+1)

        // Then remote
        compositeDisposable.add(
            vlogUseCase.like(vlogId)
                .subscribeOn(Schedulers.io())
                .subscribe(
                    {
                        vlogLikedByCurrentUser.setSuccess(true)
                    },
                    {
                        // Undo local update
                        modifyVlogLikeCount(-1)
                    }
                ))
    }

    /**
     *  Unlike a vlog as the current user.
     *
     *  Note that this also increases the [vlogLikeCount] by one
     *  and modifies the [vlogLikedByCurrentUser] resource. This
     *  is both done locally, not using the backend.
     *
     *  @param vlogId The vlog to unlike.
     */
    fun unlike(vlogId: UUID) {
        modifyVlogLikeCount(-1)

        compositeDisposable.add(
            vlogUseCase.unlike(vlogId)
                .doOnSubscribe { vlogLikedByCurrentUser.setSuccess(false) }
                .subscribeOn(Schedulers.io())
                .subscribe(
                    {
                        vlogLikedByCurrentUser.setSuccess(false)
                    },
                    {
                        modifyVlogLikeCount(+1)
                    }
                ))
    }

    // TODO Move to vlog like use case in the future
    /**
     *  Checks if a given vlog is liked by the current user.
     */
    fun isVlogLikedByCurrentUser(vlogId: UUID) =
        compositeDisposable.add(
            vlogUseCase.isVlogLikedByUser(vlogId, authUserUseCase.getSelfId())
                .doOnSubscribe { vlogLikedByCurrentUser.setLoading() }
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { vlogLikedByCurrentUser.setSuccess(it) },
                    { vlogLikedByCurrentUser.setError(it.message) }
                )
        )

    // FUTURE Fix this race condition
    /**
     *  Used to modify the [vlogLikeCount] resource. This will
     *  add the [amount] to the resource if data is present. If
     *  no data is present, this operation will be ignored. Note
     *  that this introduces a race condition where the [vlog]
     *  resource can be loading, this function can be called,
     *  and then the [vlog] resource returns. The [getVlog] will
     *  also set the [vlogLikeCount] resource, which means this
     *  addition or subtraction will get lost. This is not a
     *  problem.
     *
     *  @param amount Added to the [vlogLikeCount], negative for subtraction.
     */
    private fun modifyVlogLikeCount(amount: Int) {
        if (vlogLikeCount.value?.data == null) {
            return
        }

        vlogLikeCount.setSuccess(vlogLikeCount.value!!.data!! + amount)
    }
}
