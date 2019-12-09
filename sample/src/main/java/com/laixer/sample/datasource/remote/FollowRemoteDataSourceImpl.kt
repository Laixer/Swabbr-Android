package com.laixer.sample.datasource.remote

import com.laixer.sample.data.datasource.FollowRemoteDataSource
import com.laixer.sample.datasource.model.mapToDomain
import com.laixer.sample.domain.model.User
import io.reactivex.Single

class FollowRemoteDataSourceImpl constructor(
    private val api: FollowApi
) : FollowRemoteDataSource {

    override fun getFollowStatus(targetId: String): Single<String> =
        api.getFollowStatus(targetId)
            .map { it.mapToDomain() }

    override fun getFollowers(targetId: String): Single<List<User>> =
        api.getFollowers(targetId)
            .map { it.mapToDomain() }

    override fun getFollowing(targetId: String): Single<List<User>> =
        api.getFollowing(targetId)
            .map { it.mapToDomain() }

    override fun getIncomingRequests(): Single<List<User>> =
        api.getIncomingRequests()
            .map { it.mapToDomain() }

    override fun sendFollowRequest(targetId: String) =
        api.sendFollowRequest(targetId)

    override fun cancelFollowRequest(targetId: String) =
        api.cancelFollowRequest(targetId)

    override fun unfollow(targetId: String) =
        api.unfollow(targetId)

    override fun acceptRequest(targetId: String) =
        api.acceptRequest(targetId)

    override fun declineRequest(targetId: String) =
        api.declineRequest(targetId)
}
