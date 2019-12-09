package com.laixer.sample.datasource.remote

import com.laixer.sample.data.datasource.FollowRemoteDataSource
import com.laixer.sample.datasource.model.mapToDomain
import com.laixer.sample.domain.model.User
import io.reactivex.Single

class FollowRemoteDataSourceImpl constructor(
    private val api: FollowApi
) : FollowRemoteDataSource {

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
