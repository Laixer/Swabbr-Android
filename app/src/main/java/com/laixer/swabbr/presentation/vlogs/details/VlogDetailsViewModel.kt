package com.laixer.swabbr.presentation.vlogs.details

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.laixer.presentation.Resource
import com.laixer.presentation.setError
import com.laixer.presentation.setLoading
import com.laixer.presentation.setSuccess
import com.laixer.swabbr.domain.usecase.ReactionUseCase
import com.laixer.swabbr.domain.usecase.VlogUseCase
import com.laixer.swabbr.presentation.model.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.*

/**
 *  Viewmodel which contains details about a vlog. This contains its
 *  [VlogWrapperItem], its [ReactionItem] collection and the summary
 *  [VlogLikeSummaryItem].
 */
class VlogDetailsViewModel constructor(
    private val reactionsUseCase: ReactionUseCase,
    private val vlogUseCase: VlogUseCase
) : ViewModel() {

    val vlogs = MutableLiveData<Resource<List<VlogWrapperItem>>>()
    val reactions = MutableLiveData<Resource<List<ReactionWrapperItem>>>()
    val likes = MutableLiveData<Resource<VlogLikeSummaryItem>>()

    val watchVlogResponse = MutableLiveData<Resource<VlogWrapperItem>>()

    private val compositeDisposable = CompositeDisposable()

    /**
     *  Gets a vlog and stores it in [vlogs] as a [list]
     *  with length 1.
     *
     *  @param vlogId The vlog to get.
     *  @param refresh Force a data refresh.
     */
    fun getVlog(vlogId: UUID, refresh: Boolean = false) =
        compositeDisposable.add(
            vlogUseCase.get(vlogId, refresh)
                .doOnSubscribe { vlogs.setLoading() }
                .subscribeOn(Schedulers.io()).map { it.mapToPresentation() }
                .subscribe(
                    { vlogs.setSuccess(listOf(it)) },
                    { vlogs.setError(it.message) }
                )
        )

    /**
     *  Gets vlogs for a user and stores them in [vlogs].
     *
     *  @param vlogId The vlog to get.
     *  @param refresh Force a data refresh.
     */
    fun getVlogsForUser(userId: UUID, refresh: Boolean = false) =
        compositeDisposable.add(
            vlogUseCase.getAllForUser(userId, refresh)
                .doOnSubscribe { vlogs.setLoading() }
                .subscribeOn(Schedulers.io())
                .map { list ->
                    list.sortedByDescending { it.vlog.dateStarted }
                        .mapToPresentation()
                }
                .subscribe(
                    { vlogs.setSuccess(it) },
                    { vlogs.setError(it.message) }
                )
        )

    // TODO Can this go?
    /**
     *  This simply gets a vlog, which is probably incorrect.
     */
    fun watch(vlogId: UUID) = compositeDisposable.add(
        vlogUseCase.get(vlogId, true)
            .doOnSubscribe { watchVlogResponse.setLoading() }
            .subscribe(
                { watchVlogResponse.setSuccess(it.mapToPresentation()) },
                { watchVlogResponse.setError(it.message) }
            )
    )

    /**
     *  Get reaction for a vlog and store them in [reactions].
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
                    {
                        reactions.setSuccess(it)
                    },
                    { reactions.setError(it.message) }
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

    // TODO Is this supposed to be the vlog like summary or a list of likes?
    /**
     *  Gets a vlog like summary for a vlog. This is called after
     *  [like] and [unlike] to keep the displayed information up
     *  to date.
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

    /**
     *  Called on graceful disposal. This will dispose the [compositeDisposable]
     */
    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}
