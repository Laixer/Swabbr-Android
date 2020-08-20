package com.laixer.swabbr.data.datasource.remote

import com.laixer.swabbr.data.datasource.LivestreamDataSource
import com.laixer.swabbr.data.datasource.model.StreamResponse
import com.laixer.swabbr.data.datasource.model.WatchLivestreamResponse
import com.laixer.swabbr.data.datasource.model.remote.LivestreamApi
import io.reactivex.Single

class LivestreamDataSourceImpl constructor(
    private val livestreamApi: LivestreamApi
) : LivestreamDataSource {

    override fun startStreaming(livestreamId: String): Single<StreamResponse> =
        livestreamApi.startStreaming(livestreamId)

    override fun watch(livestreamId: String): Single<WatchLivestreamResponse> = livestreamApi.watch(livestreamId)
}
