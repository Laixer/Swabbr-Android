package com.laixer.swabbr.domain.repository

import com.laixer.swabbr.domain.model.FollowRequest
import com.laixer.swabbr.domain.model.User
import io.reactivex.Single

interface FollowRepository {

    fun getFollowStatus(userId: String): Single<FollowRequest>

    fun getFollowers(userId: String): Single<List<User>>

    fun getFollowing(userId: String): Single<List<User>>

    fun getIncomingRequests(): Single<List<User>>

    fun sendFollowRequest(userId: String): Single<FollowRequest>

    fun cancelFollowRequest(userId: String): Single<FollowRequest>

    fun unfollow(userId: String): Single<FollowRequest>

    fun acceptRequest(userId: String): Single<FollowRequest>

    fun declineRequest(userId: String): Single<FollowRequest>
}
