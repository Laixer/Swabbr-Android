package com.laixer.swabbr.domain.repository

import com.laixer.swabbr.domain.model.User
import io.reactivex.Single

interface FollowRepository {

    fun getFollowStatus(userId: String): Single<Int>

    fun getFollowers(userId: String): Single<List<User>>

    fun getFollowing(userId: String): Single<List<User>>

    fun getIncomingRequests(): Single<List<User>>

    fun sendFollowRequest(userId: String): Single<Int>

    fun cancelFollowRequest(userId: String): Single<Int>

    fun unfollow(userId: String): Single<Int>

    fun acceptRequest(userId: String): Single<Int>

    fun declineRequest(userId: String): Single<Int>
}
