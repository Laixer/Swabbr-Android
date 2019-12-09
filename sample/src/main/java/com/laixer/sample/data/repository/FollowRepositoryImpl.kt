package com.laixer.sample.data.repository

import com.laixer.sample.data.datasource.FollowRemoteDataSource
import com.laixer.sample.domain.model.User
import com.laixer.sample.domain.repository.FollowRepository
import io.reactivex.Single

class FollowRepositoryImpl constructor(
    private val remoteDataSource: FollowRemoteDataSource
) : FollowRepository {

    override fun getFollowStatus(targetId: String): Single<String> =
        remoteDataSource.getFollowStatus(targetId)

    override fun getFollowers(targetId: String): Single<List<User>> =
        remoteDataSource.getFollowers(targetId)

    override fun getFollowing(targetId: String): Single<List<User>> =
        remoteDataSource.getFollowing(targetId)

    override fun getIncomingRequests(): Single<List<User>> =
        remoteDataSource.getIncomingRequests()

    override fun sendFollowRequest(targetId: String) =
        remoteDataSource.sendFollowRequest(targetId)

    override fun cancelFollowRequest(targetId: String) =
        remoteDataSource.cancelFollowRequest(targetId)

    override fun unfollow(targetId: String) =
        remoteDataSource.unfollow(targetId)

    override fun acceptRequest(targetId: String) =
        remoteDataSource.acceptRequest(targetId)

    override fun declineRequest(targetId: String) =
        remoteDataSource.declineRequest(targetId)
}
