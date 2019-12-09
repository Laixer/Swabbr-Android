package com.laixer.sample.data.repository

import com.laixer.sample.data.datasource.FollowRemoteDataSource
import com.laixer.sample.domain.model.User
import com.laixer.sample.domain.repository.FollowRepository
import io.reactivex.Single

class FollowRepositoryImpl constructor(
    private val remoteDataSource: FollowRemoteDataSource
) : FollowRepository {

    override fun getFollowStatus(userId: String): Single<String> =
        remoteDataSource.getFollowStatus(userId)

    override fun getFollowers(userId: String): Single<List<User>> =
        remoteDataSource.getFollowers(userId)

    override fun getFollowing(userId: String): Single<List<User>> =
        remoteDataSource.getFollowing(userId)

    override fun getIncomingRequests(): Single<List<User>> =
        remoteDataSource.getIncomingRequests()

    override fun sendFollowRequest(userId: String): Single<String> =
        remoteDataSource.sendFollowRequest(userId)

    override fun cancelFollowRequest(userId: String): Single<String> =
        remoteDataSource.cancelFollowRequest(userId)

    override fun unfollow(userId: String): Single<String> =
        remoteDataSource.unfollow(userId)

    override fun acceptRequest(userId: String): Single<String> =
        remoteDataSource.acceptRequest(userId)

    override fun declineRequest(userId: String): Single<String> =
        remoteDataSource.declineRequest(userId)
}
