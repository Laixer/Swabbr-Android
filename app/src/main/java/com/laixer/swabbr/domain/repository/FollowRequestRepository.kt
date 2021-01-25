package com.laixer.swabbr.domain.repository

import com.laixer.swabbr.domain.model.FollowRequest
import io.reactivex.Completable
import io.reactivex.Single
import java.util.*

/**
 *  Interface for a follow request repository.
 */
interface FollowRequestRepository {

    fun get(requesterId: UUID, receiverId: UUID): Single<FollowRequest>

    fun getIncomingRequests(): Single<List<FollowRequest>>

    fun getOutgoingRequests(): Single<List<FollowRequest>>

    fun sendFollowRequest(userId: UUID): Completable

    fun cancelFollowRequest(userId: UUID): Completable

    fun unfollow(userId: UUID): Completable

    fun acceptRequest(userId: UUID): Completable

    fun declineRequest(userId: UUID): Completable
}
