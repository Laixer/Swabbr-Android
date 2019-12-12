package com.laixer.swabbr.datasource.remote

import com.laixer.swabbr.data.datasource.FollowDataSource
import com.laixer.swabbr.datasource.model.mapToDomain
import com.laixer.swabbr.domain.model.User
import io.reactivex.Single

class FollowDataSourceImpl constructor(
    private val api: FollowApi
) : FollowDataSource {

    override fun getFollowStatus(userId: String): Single<String> =
        api.getFollowStatus(userId)
            .map { it.mapToDomain() }

    override fun getFollowers(userId: String): Single<List<User>> =
        api.getFollowers(userId)
            .map { it.mapToDomain() }

    override fun getFollowing(userId: String): Single<List<User>> =
        api.getFollowing(userId)
            .map { it.mapToDomain() }

    override fun getIncomingRequests(): Single<List<User>> =
        api.getIncomingRequests()
            .map { it.mapToDomain() }

    override fun sendFollowRequest(userId: String): Single<String> =
        api.sendFollowRequest(userId)
            .map { it.mapToDomain() }

    override fun cancelFollowRequest(userId: String): Single<String> =
        api.cancelFollowRequest(userId)
            .map { it.mapToDomain() }

    override fun unfollow(userId: String): Single<String> =
        api.unfollow(userId)
            .map { it.mapToDomain() }

    override fun acceptRequest(userId: String): Single<String> =
        api.acceptRequest(userId)
            .map { it.mapToDomain() }

    override fun declineRequest(userId: String): Single<String> =
        api.declineRequest(userId)
            .map { it.mapToDomain() }
}