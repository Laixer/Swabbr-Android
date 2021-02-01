package com.laixer.swabbr.data.repository

import com.laixer.swabbr.data.datasource.FollowRequestCacheDataSource
import com.laixer.swabbr.data.datasource.FollowRequestDataSource
import com.laixer.swabbr.domain.model.FollowRequest
import com.laixer.swabbr.domain.repository.FollowRequestRepository
import com.laixer.swabbr.domain.types.Pagination
import io.reactivex.Completable
import io.reactivex.Single
import java.util.*

// TODO This doesn't use its cache.
/**
 *  Repository for follow requests. Note that for the actual user
 *  objects of any followers the UserRepositoryImpl should be used.
 */
class FollowRequestRepositoryImpl constructor(
    private val cacheDataSource: FollowRequestCacheDataSource,
    private val remoteDataSource: FollowRequestDataSource
    ) : FollowRequestRepository {
    override fun get(requesterId: UUID, receiverId: UUID): Single<FollowRequest> =
        remoteDataSource.get(requesterId, receiverId)

    override fun getIncomingRequests(pagination: Pagination): Single<List<FollowRequest>> =
        remoteDataSource.getIncomingRequests(pagination)

    override fun getOutgoingRequests(pagination: Pagination): Single<List<FollowRequest>> =
        remoteDataSource.getOutgoingRequests(pagination)

    override fun sendFollowRequest(userId: UUID): Completable = remoteDataSource.sendFollowRequest(userId)

    override fun cancelFollowRequest(userId: UUID): Completable = remoteDataSource.cancelFollowRequest(userId)

    override fun unfollow(userId: UUID): Completable = remoteDataSource.unfollow(userId)

    override fun acceptRequest(userId: UUID): Completable = remoteDataSource.acceptRequest(userId)

    override fun declineRequest(userId: UUID): Completable = remoteDataSource.declineRequest(userId)
}
