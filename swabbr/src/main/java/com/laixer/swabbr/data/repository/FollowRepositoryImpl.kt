package com.laixer.swabbr.data.repository

import com.laixer.swabbr.data.datasource.FollowDataSource
import com.laixer.swabbr.domain.model.FollowRequest
import com.laixer.swabbr.domain.model.User
import com.laixer.swabbr.domain.repository.FollowRepository
import io.reactivex.Single
import java.util.UUID

class FollowRepositoryImpl constructor(
    private val dataSource: FollowDataSource
) : FollowRepository {

    override fun getFollowStatus(userId: UUID): Single<FollowRequest> = dataSource.getFollowStatus(userId)

    override fun getFollowers(userId: UUID): Single<List<User>> = dataSource.getFollowers(userId)

    override fun getFollowing(userId: UUID): Single<List<User>> = dataSource.getFollowing(userId)

    override fun getIncomingRequests(): Single<List<User>> = dataSource.getIncomingRequests()

    override fun sendFollowRequest(userId: UUID): Single<FollowRequest> = dataSource.sendFollowRequest(userId)

    override fun cancelFollowRequest(userId: UUID): Single<FollowRequest> = dataSource.cancelFollowRequest(userId)

    override fun unfollow(userId: UUID): Single<FollowRequest> = dataSource.unfollow(userId)

    override fun acceptRequest(userId: UUID): Single<FollowRequest> = dataSource.acceptRequest(userId)

    override fun declineRequest(userId: UUID): Single<FollowRequest> = dataSource.declineRequest(userId)
}
