package com.laixer.swabbr.presentation.recording

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.laixer.presentation.Resource
import com.laixer.presentation.setError
import com.laixer.presentation.setLoading
import com.laixer.presentation.setSuccess
import com.laixer.swabbr.datasource.model.StreamResponse
import com.laixer.swabbr.datasource.model.WatchResponse
import com.laixer.swabbr.domain.model.FollowStatus
import com.laixer.swabbr.domain.usecase.FollowUseCase
import com.laixer.swabbr.domain.usecase.LivestreamUseCase
import com.laixer.swabbr.domain.usecase.UserVlogsUseCase
import com.laixer.swabbr.domain.usecase.UsersUseCase
import com.laixer.swabbr.presentation.model.FollowStatusItem
import com.laixer.swabbr.presentation.model.UserItem
import com.laixer.swabbr.presentation.model.VlogItem
import com.laixer.swabbr.presentation.model.mapToPresentation
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.UUID

class LivestreamViewModel constructor(
    private val livestreamUseCase: LivestreamUseCase
) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    fun startStreaming(livestreamId: String): Single<StreamResponse> = livestreamUseCase.startStreaming(livestreamId)

    fun watch(livestreamId: String): Single<WatchResponse> = livestreamUseCase.watch(livestreamId)

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}
