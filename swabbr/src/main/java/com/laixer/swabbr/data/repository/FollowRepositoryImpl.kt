package com.laixer.swabbr.data.repository

import com.laixer.swabbr.data.datasource.FollowDataSource
import com.laixer.swabbr.domain.model.User
import com.laixer.swabbr.domain.repository.FollowRepository
import io.reactivex.Single

class FollowRepositoryImpl constructor(
    private val dataSource: FollowDataSource
) : FollowRepository {

    override fun getFollowStatus(userId: String): Single<Int> =
        dataSource.getFollowStatus(userId)

    override fun getFollowers(userId: String): Single<List<User>> =
        dataSource.getFollowers(userId)

    override fun getFollowing(userId: String): Single<List<User>> =
        dataSource.getFollowing(userId)

    override fun getIncomingRequests(): Single<List<User>> =
        dataSource.getIncomingRequests()

    override fun sendFollowRequest(userId: String): Single<Int> =
        dataSource.sendFollowRequest(userId)

    override fun cancelFollowRequest(userId: String): Single<Int> =
        dataSource.cancelFollowRequest(userId)

    override fun unfollow(userId: String): Single<Int> =
        dataSource.unfollow(userId)

    override fun acceptRequest(userId: String): Single<Int> =
        dataSource.acceptRequest(userId)

    override fun declineRequest(userId: String): Single<Int> =
        dataSource.declineRequest(userId)
}
