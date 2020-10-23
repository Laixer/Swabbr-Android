package com.laixer.swabbr.presentation.livestream

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.laixer.presentation.Resource
import com.laixer.presentation.setError
import com.laixer.presentation.setLoading
import com.laixer.presentation.setSuccess
import com.laixer.swabbr.data.datasource.model.StreamResponse
import com.laixer.swabbr.data.datasource.model.WatchLivestreamResponse
import com.laixer.swabbr.domain.usecase.LivestreamUseCase
import io.reactivex.disposables.CompositeDisposable

class LivestreamViewModel constructor(
    private val livestreamUseCase: LivestreamUseCase
) : ViewModel() {

    val streamResponse = MutableLiveData<Resource<StreamResponse>>()
    val watchResponse = MutableLiveData<Resource<WatchLivestreamResponse>>()

    private val compositeDisposable = CompositeDisposable()

    fun startStreaming(livestreamId: String) =
        compositeDisposable.add(
            livestreamUseCase.startStreaming(livestreamId)
                .doOnSubscribe { streamResponse.setLoading() }
                .subscribe(
                    { streamResponse.setSuccess(it) },
                    { streamResponse.setError(it.message) }
                )
        )

    fun watch(livestreamId: String) =
        compositeDisposable.add(
            livestreamUseCase.watch(livestreamId)
                .doOnSubscribe { watchResponse.setLoading() }
                .subscribe(
                    { watchResponse::setSuccess },
                    { watchResponse.setError(it.message) }
                )
        )

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}
