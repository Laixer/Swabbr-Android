package com.laixer.sample.data.repository

import com.laixer.sample.data.datasource.FollowDataSource
import com.laixer.sample.domain.model.User
import com.laixer.sample.domain.repository.FollowRepository
import io.reactivex.Single

class FollowRepositoryImpl constructor(
    private val dataSource: FollowDataSource
) : FollowRepository {

    override fun getFollowStatus(userId: String): Single<String> =
        dataSource.getFollowStatus(userId)

    override fun getFollowers(userId: String): Single<List<User>> =
        dataSource.getFollowers(userId)

    override fun getFollowing(userId: String): Single<List<User>> =
        dataSource.getFollowing(userId)

    override fun getIncomingRequests(): Single<List<User>> =
        dataSource.getIncomingRequests()

    override fun sendFollowRequest(userId: String): Single<String> =
        dataSource.sendFollowRequest(userId)

    override fun cancelFollowRequest(userId: String): Single<String> =
        dataSource.cancelFollowRequest(userId)

    override fun unfollow(userId: String): Single<String> =
        dataSource.unfollow(userId)

    override fun acceptRequest(userId: String): Single<String> =
        dataSource.acceptRequest(userId)

    override fun declineRequest(userId: String): Single<String> =
        dataSource.declineRequest(userId)
}
