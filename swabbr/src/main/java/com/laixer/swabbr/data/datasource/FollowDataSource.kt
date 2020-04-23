package com.laixer.swabbr.data.datasource

import com.laixer.swabbr.domain.model.FollowRequest
import com.laixer.swabbr.domain.model.User
import io.reactivex.Single
import java.util.UUID

interface FollowDataSource {

    val key: String
        get() = "FOLLOWS"

    fun getFollowStatus(userId: UUID): Single<FollowRequest>

    fun getFollowers(userId: UUID): Single<List<User>>

    fun getFollowing(userId: UUID): Single<List<User>>

    fun getIncomingRequests(): Single<List<User>>

    fun sendFollowRequest(userId: UUID): Single<FollowRequest>

    fun cancelFollowRequest(userId: UUID): Single<FollowRequest>

    fun unfollow(userId: UUID): Single<FollowRequest>

    fun acceptRequest(userId: UUID): Single<FollowRequest>

    fun declineRequest(userId: UUID): Single<FollowRequest>
}
