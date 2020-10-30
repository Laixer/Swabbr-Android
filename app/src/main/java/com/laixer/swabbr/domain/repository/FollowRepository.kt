package com.laixer.swabbr.domain.repository

import com.laixer.swabbr.domain.model.FollowRequest
import com.laixer.swabbr.domain.model.FollowStatus
import com.laixer.swabbr.domain.model.User
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

    fun acceptRequest(userId: UUID): Completable

    fun declineRequest(userId: UUID): Completable

    fun getFollowing(userId: UUID, refresh: Boolean = false): Single<List<User>>

    fun getFollowers(userId: UUID, refresh: Boolean = false): Single<List<User>>

}
