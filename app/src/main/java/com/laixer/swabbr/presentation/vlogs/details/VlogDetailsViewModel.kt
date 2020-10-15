package com.laixer.swabbr.presentation.vlogs.details

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.laixer.presentation.Resource
import com.laixer.presentation.setError
import com.laixer.presentation.setLoading
import com.laixer.presentation.setSuccess
import com.laixer.swabbr.data.datasource.model.WatchVlogResponse
import com.laixer.swabbr.domain.usecase.UserReactionUseCase
import com.laixer.swabbr.domain.usecase.UserVlogUseCase
import com.laixer.swabbr.domain.usecase.UserVlogsUseCase
import com.laixer.swabbr.domain.usecase.VlogsUseCase
import com.laixer.swabbr.presentation.model.LikeListItem
import com.laixer.swabbr.presentation.model.ReactionItem
import com.laixer.swabbr.presentation.model.UserVlogItem
import com.laixer.swabbr.presentation.model.mapToPresentation
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.UUID

class VlogDetailsViewModel constructor(
    private val userVlogsUseCase: UserVlogsUseCase,
    private val reactionsUseCase: UserReactionUseCase,
    private val userVlogUseCase: UserVlogUseCase,
    private val vlogsUseCase: VlogsUseCase
) : ViewModel() {

    val vlogs = MutableLiveData<Resource<List<UserVlogItem>>>()
    val reactions = MutableLiveData<Resource<List<ReactionItem>>>()
    val likes = MutableLiveData<Resource<LikeListItem>>()
    val watchResponse = MutableLiveData<Resource<WatchVlogResponse>>()

    private val compositeDisposable = CompositeDisposable()

    fun getVlog(vlogId: UUID, refresh: Boolean = false) =
        compositeDisposable.add(
            userVlogUseCase.get(vlogId, refresh)
                .doOnSubscribe { vlogs.setLoading() }
                .subscribeOn(Schedulers.io()).map { it.mapToPresentation() }
                .subscribe(
                    { vlogs.setSuccess(listOf(it)) },
                    { vlogs.setError(it.message) }
                )
        )

    fun getVlogs(userId: UUID, refresh: Boolean = false) =
        compositeDisposable.add(
            userVlogsUseCase.get(userId, refresh)
                .doOnSubscribe { vlogs.setLoading() }
                .subscribeOn(Schedulers.io())
                .map { list ->
                    list.sortedByDescending { it.second.data.dateStarted }
                        .mapToPresentation()
                }
                .subscribe(
                    { vlogs.setSuccess(it) },
                    { vlogs.setError(it.message) }
                )
        )

    fun watch(vlogId: UUID) = compositeDisposable.add(
        vlogsUseCase.watch(vlogId)
            .doOnSubscribe { watchResponse.setLoading() }
            .subscribe(
                { watchResponse.setSuccess(it) },
                { watchResponse.setError(it.message) }
            )
    )

    fun getReactions(vlogId: UUID, refresh: Boolean = false) =
        compositeDisposable.add(
            reactionsUseCase.get(vlogId, refresh)
                .doOnSubscribe { reactions.setLoading() }
                .subscribeOn(Schedulers.io()).map { it.mapToPresentation() }
                .subscribe(
                    { reactions.setSuccess(it) },
                    { reactions.setError(it.message) }
                )
        )

    fun like(vlogId: UUID) = vlogsUseCase.like(vlogId)
        .doOnSubscribe { likes.setLoading() }
        .subscribeOn(Schedulers.io())
        .subscribe(
            { getLikes(vlogId) },
            { likes.setError(it.message) }
        )

    fun unlike(vlogId: UUID) = vlogsUseCase.unlike(vlogId)
        .doOnSubscribe { likes.setLoading() }
        .subscribeOn(Schedulers.io())
        .subscribe(
            { getLikes(vlogId) },
            { likes.setError(it.message) }
        )

    fun getLikes(vlogId: UUID) =
        compositeDisposable.add(
            vlogsUseCase.getLikes(vlogId)
                .doOnSubscribe { likes.setLoading() }
                .subscribeOn(Schedulers.io())
                .map { it.mapToPresentation() }
                .subscribe(
                    { likes.setSuccess(it) },
                    { likes.setError(it.message) }
                )
        )

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}
