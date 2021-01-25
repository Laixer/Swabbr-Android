package com.laixer.swabbr.presentation.streaming

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.laixer.presentation.Resource
import com.laixer.presentation.setError
import com.laixer.presentation.setLoading
import com.laixer.presentation.setSuccess
import com.laixer.swabbr.domain.usecase.VlogUseCase
import io.reactivex.disposables.CompositeDisposable

class StreamViewModel constructor(
    private val vlogUseCase: VlogUseCase
) : ViewModel() {

//    val streamResponse = MutableLiveData<Resource<StreamResponse>>()
//    val watchResponse = MutableLiveData<Resource<WatchLivestreamResponse>>()

    private val compositeDisposable = CompositeDisposable()

    fun startStreaming(livestreamId: String): Nothing = throw NotImplementedError()
//        compositeDisposable.add(
//            livestreamUseCase.startStreaming(livestreamId)
//                .doOnSubscribe { streamResponse.setLoading() }
//                .subscribe(
//                    { streamResponse.setSuccess(it) },
//                    { streamResponse.setError(it.message) }
//                )
//        )

    fun watch(livestreamId: String): Nothing = throw NotImplementedError()
//        compositeDisposable.add(
//            livestreamUseCase.watch(livestreamId)
//                .doOnSubscribe { watchResponse.setLoading() }
//                .subscribe(
//                    { watchResponse::setSuccess },
//                    { watchResponse.setError(it.message) }
//                )
//        )

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}
