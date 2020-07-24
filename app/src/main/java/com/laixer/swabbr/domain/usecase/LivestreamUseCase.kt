package com.laixer.swabbr.domain.usecase

import com.laixer.swabbr.datasource.model.StreamResponse
import com.laixer.swabbr.datasource.model.WatchResponse
import com.laixer.swabbr.domain.model.AuthUser
import com.laixer.swabbr.domain.model.Login
import com.laixer.swabbr.domain.model.Registration
import com.laixer.swabbr.domain.repository.AuthRepository
import com.laixer.swabbr.domain.repository.LivestreamRepository
import io.reactivex.Completable
import io.reactivex.Single

class LivestreamUseCase constructor(
    private val livestreamRepository: LivestreamRepository
) {

    fun startStreaming(livestreamId: String): Single<StreamResponse> = livestreamRepository.startStreaming(livestreamId)

    fun watch(livestreamId: String): Single<WatchResponse> = livestreamRepository.watch(livestreamId)
}
