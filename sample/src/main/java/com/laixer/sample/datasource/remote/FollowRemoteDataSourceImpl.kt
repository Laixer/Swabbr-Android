package com.laixer.sample.datasource.remote

import com.laixer.sample.data.datasource.FollowRemoteDataSource
import com.laixer.sample.datasource.model.mapToDomain
import com.laixer.sample.domain.model.FollowRequest
import io.reactivex.Single

class FollowRemoteDataSourceImpl constructor(
    private val api: FollowApi
) : FollowRemoteDataSource {

    override fun get(receiverId: String): Single<List<FollowRequest>> =
        api.getFollowRequests(receiverId)
            .map { it.mapToDomain() }

    override fun get(requesterId: String, receiverId: String): Single<FollowRequest> =
        api.getFollowRequest(requesterId, receiverId)
            .map { it.mapToDomain() }

    override fun set(followRequest: FollowRequest): Single<FollowRequest> =
        // TODO: Aanvullen / Fixen
        api.sendRequest("", "")
            .map { it.mapToDomain() }
}
