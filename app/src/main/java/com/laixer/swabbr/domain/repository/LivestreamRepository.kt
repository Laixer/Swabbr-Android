package com.laixer.swabbr.domain.repository

import com.laixer.swabbr.data.datasource.model.StreamResponse
import com.laixer.swabbr.data.datasource.model.WatchLivestreamResponse
import io.reactivex.Single

interface LivestreamRepository {

    fun startStreaming(livestreamId: String): Single<StreamResponse>

    fun watch(livestreamId: String): Single<WatchLivestreamResponse>
}
