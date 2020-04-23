package com.laixer.swabbr.data.datasource.remote

import com.laixer.swabbr.data.datasource.FollowDataSource
import com.laixer.swabbr.datasource.model.mapToDomain
import com.laixer.swabbr.datasource.model.remote.FollowApi
import com.laixer.swabbr.domain.model.FollowRequest
import com.laixer.swabbr.domain.model.User
import io.reactivex.Single
import java.util.UUID

class FollowDataSourceImpl constructor(
    val api: FollowApi
) : FollowDataSource {

    override fun getFollowStatus(userId: UUID): Single<FollowRequest> =
        api.getFollowRequest(userId).map { it.mapToDomain() }

    override fun getFollowers(userId: UUID): Single<List<User>> = api.getFollowers(userId).map { it.mapToDomain() }

    override fun getFollowing(userId: UUID): Single<List<User>> = api.getFollowing(userId).map { it.mapToDomain() }

    override fun getIncomingRequests(): Single<List<User>> = api.getIncomingRequests().map { it.mapToDomain() }

    override fun sendFollowRequest(userId: UUID): Single<FollowRequest> =
        api.sendFollowRequest(userId).map { it.mapToDomain() }

    override fun cancelFollowRequest(userId: UUID): Single<FollowRequest> =
        api.cancelFollowRequest(userId).map { it.mapToDomain() }

    override fun unfollow(userId: UUID): Single<FollowRequest> = api.unfollow(userId).map { it.mapToDomain() }

    override fun acceptRequest(userId: UUID): Single<FollowRequest> = api.acceptRequest(userId).map { it.mapToDomain() }

    override fun declineRequest(userId: UUID): Single<FollowRequest> =
        api.declineRequest(userId).map { it.mapToDomain() }
}
