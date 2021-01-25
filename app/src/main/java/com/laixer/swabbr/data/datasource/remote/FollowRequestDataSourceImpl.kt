package com.laixer.swabbr.data.datasource.remote

import com.laixer.swabbr.data.datasource.FollowRequestDataSource
import com.laixer.swabbr.data.datasource.model.mapToDomain
import com.laixer.swabbr.data.datasource.model.remote.FollowRequestApi
import com.laixer.swabbr.domain.model.FollowRequest
import io.reactivex.Completable
import io.reactivex.Single
import java.util.*

class FollowRequestRemoteDataSourceImpl constructor(
    val api: FollowRequestApi
) : FollowRequestDataSource {
    override fun get(requesterId: UUID, receiverId: UUID): Single<FollowRequest> =
        api.get(requesterId, receiverId).map { it.mapToDomain() }

    override fun getIncomingRequests(): Single<List<FollowRequest>> =
        api.getIncomingRequests().map { it.mapToDomain() }

    override fun getOutgoingRequests(): Single<List<FollowRequest>> =
        api.getOutgoingRequests().map { it.mapToDomain() }

    override fun sendFollowRequest(userId: UUID): Completable =
        api.sendFollowRequest(userId)

    override fun cancelFollowRequest(userId: UUID): Completable = api.cancelFollowRequest(userId)

    override fun unfollow(userId: UUID): Completable = api.unfollow(userId)

    override fun acceptRequest(userId: UUID): Completable = api.acceptRequest(userId)

    override fun declineRequest(userId: UUID): Completable = api.declineRequest(userId)
}
