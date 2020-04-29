package com.laixer.swabbr.data.datasource.remote

import com.laixer.swabbr.data.datasource.FollowDataSource
import com.laixer.swabbr.datasource.model.mapToDomain
import com.laixer.swabbr.datasource.model.remote.FollowApi
import com.laixer.swabbr.domain.model.FollowRequest
import com.laixer.swabbr.domain.model.FollowStatus
import com.laixer.swabbr.domain.model.User
import io.reactivex.Completable
import io.reactivex.Single
import java.util.UUID

class FollowDataSourceImpl constructor(
    val api: FollowApi
) : FollowDataSource {

    override fun getFollowStatus(userId: UUID): Single<FollowStatus> =
        api.getFollowStatus(userId).map { it.mapToDomain() }

    override fun getIncomingRequests(): Single<List<FollowRequest>> = api.getIncomingRequests().map { it.mapToDomain() }

    override fun getOutgoingRequests(): Single<List<FollowRequest>> = api.getOutgoingRequests().map { it.mapToDomain() }

    override fun sendFollowRequest(userId: UUID): Single<FollowRequest> =
        api.sendFollowRequest(userId).map { it.mapToDomain() }

    override fun cancelFollowRequest(userId: UUID): Completable = api.cancelFollowRequest(userId)

    override fun unfollow(userId: UUID): Completable = api.unfollow(userId)

    override fun acceptRequest(userId: UUID): Single<FollowRequest> = api.acceptRequest(userId).map { it.mapToDomain() }

    override fun declineRequest(userId: UUID): Single<FollowRequest> =
        api.declineRequest(userId).map { it.mapToDomain() }
}
