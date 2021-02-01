package com.laixer.swabbr.data.datasource

import com.laixer.swabbr.domain.model.FollowRequest
import com.laixer.swabbr.domain.types.Pagination
import io.reactivex.Completable
import io.reactivex.Single
import java.util.*

/**
 *  Caching for follow requests.
 */
interface FollowRequestCacheDataSource {
    val keyIncoming: String get() = "incomingFollowRequests"

    val keyOutgoing: String get() = "outgoingFollowRequests"

    fun getIncomingRequests(): Single<List<FollowRequest>>

    fun getOutgoingRequests(): Single<List<FollowRequest>>

    fun setIncomingRequests(followRequests: List<FollowRequest>): Single<List<FollowRequest>>

    fun setOutgoingRequests(followRequests: List<FollowRequest>): Single<List<FollowRequest>>
}

/**
 *  Data source for follow requests.
 */
interface FollowRequestDataSource {
    fun get(requesterId: UUID, receiverId: UUID): Single<FollowRequest>

    fun getIncomingRequests(pagination: Pagination = Pagination.latest()): Single<List<FollowRequest>>

    fun getOutgoingRequests(pagination: Pagination = Pagination.latest()): Single<List<FollowRequest>>

    fun sendFollowRequest(userId: UUID): Completable

    fun cancelFollowRequest(userId: UUID): Completable

    fun unfollow(userId: UUID): Completable

    fun acceptRequest(userId: UUID): Completable

    fun declineRequest(userId: UUID): Completable
}
