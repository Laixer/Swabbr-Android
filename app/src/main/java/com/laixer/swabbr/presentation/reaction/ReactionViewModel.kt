package com.laixer.swabbr.presentation.reaction

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.laixer.presentation.Resource
import com.laixer.presentation.setError
import com.laixer.presentation.setLoading
import com.laixer.presentation.setSuccess
import com.laixer.swabbr.data.datasource.model.WatchReactionResponse
import com.laixer.swabbr.data.datasource.model.WatchVlogResponse
import com.laixer.swabbr.domain.usecase.UserReactionUseCase
import com.laixer.swabbr.domain.usecase.UserVlogUseCase
import com.laixer.swabbr.domain.usecase.UserVlogsUseCase
import com.laixer.swabbr.domain.usecase.VlogsUseCase
import com.laixer.swabbr.presentation.model.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.UUID

class ReactionViewModel constructor(
    private val reactionsUseCase: UserReactionUseCase
) : ViewModel() {

    val newReaction = MutableLiveData<Resource<UploadReactionItem>>()
    val watchReactionResponse = MutableLiveData<Resource<WatchReactionResponse>>()

    private val compositeDisposable = CompositeDisposable()

    fun watch(reactionId: UUID) = compositeDisposable.add(
        reactionsUseCase.watch(reactionId)
            .doOnSubscribe { watchReactionResponse.setLoading() }
            .subscribeOn(Schedulers.io())
            .subscribe(
                { watchReactionResponse.setSuccess(it) },
                { watchReactionResponse.setError(it.message)}
            )
    )

    fun newReaction(targetVlogId: UUID) = compositeDisposable.add(
        reactionsUseCase.new(targetVlogId)
            .doOnSubscribe { newReaction.setLoading() }
            .subscribeOn(Schedulers.io())
            .map { it.mapToPresentation() }
            .subscribe(
                { newReaction.setSuccess(it) },
                { newReaction.setError(it.message) }
            )
    )

    fun finishUploading(reactionId: UUID, onComplete: () -> Unit, onError: () -> Unit) = compositeDisposable.add(
        reactionsUseCase.finishUploading(reactionId)
            .doOnSubscribe { }
            .subscribeOn(Schedulers.io())
            .subscribe({ onComplete.invoke() }, { onError.invoke() })
    )

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}
