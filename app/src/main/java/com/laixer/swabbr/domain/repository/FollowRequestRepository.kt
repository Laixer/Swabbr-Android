package com.laixer.swabbr.domain.repository

import com.laixer.swabbr.domain.model.FollowRequest
import com.laixer.swabbr.domain.types.Pagination
import io.reactivex.Completable
import io.reactivex.Single
import java.util.*

/**
 *  Interface for a follow request repository.
 */
interface FollowRequestRepository {

    fun get(requesterId: UUID, receiverId: UUID): Single<FollowRequest>

    fun getIncomingRequests(pagination: Pagination = Pagination.latest()): Single<List<FollowRequest>>

    fun getOutgoingRequests(pagination: Pagination = Pagination.latest()): Single<List<FollowRequest>>

    fun sendFollowRequest(userId: UUID): Completable

    fun cancelFollowRequest(userId: UUID): Completable

    fun unfollow(userId: UUID): Completable

    fun acceptRequest(userId: UUID): Completable

    fun declineRequest(userId: UUID): Completable
}
