package com.laixer.swabbr.data.repository

import com.laixer.swabbr.data.datasource.FollowCacheDataSource
import com.laixer.swabbr.data.datasource.FollowRemoteDataSource
import com.laixer.swabbr.domain.model.FollowRequest
import com.laixer.swabbr.domain.model.FollowStatus
import com.laixer.swabbr.domain.model.User
import com.laixer.swabbr.domain.repository.FollowRepository
import io.reactivex.Completable
import io.reactivex.Single
import java.util.UUID

class FollowRepositoryImpl constructor(
    private val remoteDataSource: FollowRemoteDataSource,
    private val cacheDataSource: FollowCacheDataSource
) : FollowRepository {

    override fun getFollowStatus(userId: UUID): Single<FollowStatus> = remoteDataSource.getFollowStatus(userId)

    override fun getIncomingRequests(): Single<List<FollowRequest>> = remoteDataSource.getIncomingRequests()

    override fun getOutgoingRequests(): Single<List<FollowRequest>> = remoteDataSource.getOutgoingRequests()

    override fun sendFollowRequest(userId: UUID): Single<FollowRequest> = remoteDataSource.sendFollowRequest(userId)

    override fun cancelFollowRequest(userId: UUID): Completable = remoteDataSource.cancelFollowRequest(userId)

    override fun unfollow(userId: UUID): Completable = remoteDataSource.unfollow(userId)

    override fun acceptRequest(userId: UUID): Single<FollowRequest> = remoteDataSource.acceptRequest(userId)

    override fun declineRequest(userId: UUID): Single<FollowRequest> = remoteDataSource.declineRequest(userId)

    override fun getFollowers(userId: UUID, refresh: Boolean): Single<List<User>> = when (refresh) {
        true -> remoteDataSource.getFollowers(userId).flatMap { cacheDataSource.setFollowers(userId, it) }
        false -> cacheDataSource.getFollowers(userId).onErrorResumeNext { getFollowers(userId, true) }
    }

    override fun getFollowing(userId: UUID, refresh: Boolean): Single<List<User>> = when (refresh) {
        true -> remoteDataSource.getFollowing(userId).flatMap { cacheDataSource.setFollowing(userId, it) }
        false -> cacheDataSource.getFollowing(userId).onErrorResumeNext { getFollowing(userId, true) }
    }
}
