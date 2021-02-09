package com.laixer.swabbr.presentation.vlogs.playback

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.laixer.presentation.Resource
import com.laixer.presentation.setError
import com.laixer.presentation.setLoading
import com.laixer.presentation.setSuccess
import com.laixer.swabbr.domain.usecase.AuthUserUseCase
import com.laixer.swabbr.domain.usecase.ReactionUseCase
import com.laixer.swabbr.domain.usecase.VlogUseCase
import com.laixer.swabbr.presentation.model.ReactionWrapperItem
import com.laixer.swabbr.presentation.model.VlogLikeSummaryItem
import com.laixer.swabbr.presentation.model.VlogWrapperItem
import com.laixer.swabbr.presentation.model.mapToPresentation
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.*

/**
 *  View model which contains details about a single vlog.
 */
class VlogViewModel constructor(
    private val authUserUseCase: AuthUserUseCase,
    private val reactionsUseCase: ReactionUseCase,
    private val vlogUseCase: VlogUseCase
) : ViewModel() {
    /**
     *  Used to store a vlog when we watch a single item.
     */
    val vlog = MutableLiveData<Resource<VlogWrapperItem>>()

    /**
     *  Used to store the result of any vlog list retrieval.
     */
    val vlogs = MutableLiveData<Resource<List<VlogWrapperItem>>>()

    /**
     *  Used to store reactions for the [vlog] resource.
     */
    val reactions = MutableLiveData<Resource<List<ReactionWrapperItem>>>()

    /**
     *  Used to store the amount of reactions for the [vlog] resource.
     */
    val reactionCount = MutableLiveData<Resource<Int>>()

    /**
     *  Used to store likes for the [vlog] resource.
     */
    val likes = MutableLiveData<Resource<VlogLikeSummaryItem>>()

    /**
     *  Used to store whether or not the current user has
     *  liked the [vlog] resource.
     */
    val likedByCurrentUser = MutableLiveData<Resource<Boolean>>()

    private val compositeDisposable = CompositeDisposable()

    /**
     *  Gets a vlog and stores it in [vlogs].
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
                    { vlog.setSuccess(it) },
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
     *  Like a vlog, then call [getVlogLikeSummary] to update
     *  the displayed information.
     *
     *  @param vlogId The vlog to like.
     */
    fun like(vlogId: UUID) = vlogUseCase.like(vlogId)
        .doOnSubscribe { likes.setLoading() }
        .subscribeOn(Schedulers.io())
        .subscribe(
            { getVlogLikeSummary(vlogId) },
            { likes.setError(it.message) }
        )

    /**
     *  Unlike a vlog, then call [getVlogLikeSummary] to update
     *  the displayed information.
     *
     *  @param vlogId The vlog to unlike.
     */
    fun unlike(vlogId: UUID) = vlogUseCase.unlike(vlogId)
        .doOnSubscribe { likes.setLoading() }
        .subscribeOn(Schedulers.io())
        .subscribe(
            { getVlogLikeSummary(vlogId) },
            { likes.setError(it.message) }
        )

    // TODO This should be a list of likes. The summary already exists in the vlog itself.
    //  I think we can simply remove this for now, or move it to the likes tab?
    /**
     *  Gets a vlog like summary for a vlog. This is  also called after
     *  [like] and [unlike] to keep the displayed information up
     *  to date. Note that the likes are expected to belong to the
     *  [vlog] resource.
     *
     *  @param vlogId The vlog to get the summary for.
     */
    fun getVlogLikeSummary(vlogId: UUID) =
        compositeDisposable.add(
            vlogUseCase.getVlogLikeSummary(vlogId)
                .doOnSubscribe { likes.setLoading() }
                .subscribeOn(Schedulers.io())
                .map { it.mapToPresentation() }
                .subscribe(
                    { likes.setSuccess(it) },
                    { likes.setError(it.message) }
                )
        )

    // TODO Move to vlog like use case in the future
    /**
     *  Checks if a given vlog is liked by the current user.
     */
    fun isVlogLikedByCurrentUser(vlogId: UUID) =
        compositeDisposable.add(
            vlogUseCase.isVlogLikedByUser(vlogId, authUserUseCase.getSelfId())
                .doOnSubscribe { likedByCurrentUser.setLoading() }
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { likedByCurrentUser.setSuccess(it) },
                    { likedByCurrentUser.setError(it.message) }
                )
        )

    /**
     *  Called on graceful disposal. This will dispose the [compositeDisposable]
     */
    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}
