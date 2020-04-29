package com.laixer.swabbr.domain.repository

import com.laixer.swabbr.domain.model.FollowRequest
import com.laixer.swabbr.domain.model.FollowStatus
import io.reactivex.Completable
import io.reactivex.Single
import java.util.UUID

interface FollowRepository {

    fun getFollowStatus(userId: UUID): Single<FollowStatus>

    fun getIncomingRequests(): Single<List<FollowRequest>>

    fun getOutgoingRequests(): Single<List<FollowRequest>>

    fun sendFollowRequest(userId: UUID): Single<FollowRequest>

    fun cancelFollowRequest(userId: UUID): Completable

    fun unfollow(userId: UUID): Completable

    fun acceptRequest(userId: UUID): Single<FollowRequest>

    fun declineRequest(userId: UUID): Single<FollowRequest>
}
