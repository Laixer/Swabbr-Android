package com.laixer.swabbr.data.repository

import com.laixer.swabbr.data.datasource.FollowDataSource
import com.laixer.swabbr.domain.model.FollowRequest
import com.laixer.swabbr.domain.model.FollowStatus
import com.laixer.swabbr.domain.model.User
import com.laixer.swabbr.domain.repository.FollowRepository
import io.reactivex.Completable
import io.reactivex.Single
import java.util.UUID

class FollowRepositoryImpl constructor(
    private val dataSource: FollowDataSource
) : FollowRepository {

    override fun getFollowStatus(userId: UUID): Single<FollowStatus> = dataSource.getFollowStatus(userId)

    override fun getIncomingRequests(): Single<List<FollowRequest>> = dataSource.getIncomingRequests()

    override fun getOutgoingRequests(): Single<List<FollowRequest>> = dataSource.getOutgoingRequests()

    override fun sendFollowRequest(userId: UUID): Single<FollowRequest> = dataSource.sendFollowRequest(userId)

    override fun cancelFollowRequest(userId: UUID): Completable = dataSource.cancelFollowRequest(userId)

    override fun unfollow(userId: UUID): Completable = dataSource.unfollow(userId)

    override fun acceptRequest(userId: UUID): Single<FollowRequest> = dataSource.acceptRequest(userId)

    override fun declineRequest(userId: UUID): Single<FollowRequest> = dataSource.declineRequest(userId)
}
