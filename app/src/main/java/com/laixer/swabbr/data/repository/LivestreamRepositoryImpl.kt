package com.laixer.swabbr.data.repository

import com.laixer.swabbr.data.datasource.LivestreamDataSource
import com.laixer.swabbr.data.datasource.model.StreamResponse
import com.laixer.swabbr.data.datasource.model.WatchLivestreamResponse
import com.laixer.swabbr.domain.repository.LivestreamRepository
import io.reactivex.Single

class LivestreamRepositoryImpl constructor(
    private val livestreamDataSource: LivestreamDataSource
) : LivestreamRepository {

    override fun startStreaming(livestreamId: String): Single<StreamResponse> = livestreamDataSource.startStreaming(livestreamId)

    override fun watch(livestreamId: String): Single<WatchLivestreamResponse> = livestreamDataSource.watch(livestreamId)
}
