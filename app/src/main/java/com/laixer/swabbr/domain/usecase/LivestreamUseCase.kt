package com.laixer.swabbr.domain.usecase

import com.laixer.swabbr.data.datasource.model.StreamResponse
import com.laixer.swabbr.data.datasource.model.WatchLivestreamResponse
import com.laixer.swabbr.domain.repository.LivestreamRepository
import io.reactivex.Single

class LivestreamUseCase constructor(
    private val livestreamRepository: LivestreamRepository
) {

    fun startStreaming(livestreamId: String): Single<StreamResponse> = livestreamRepository.startStreaming(livestreamId)

    fun watch(livestreamId: String): Single<WatchLivestreamResponse> = livestreamRepository.watch(livestreamId)

}
