package com.laixer.sample.data.datasource

import com.laixer.sample.domain.model.User
import io.reactivex.Single

interface FollowRemoteDataSource {

    fun getFollowStatus(userId: String): Single<String>

    fun getFollowers(userId: String): Single<List<User>>

    fun getFollowing(userId: String): Single<List<User>>

    fun getIncomingRequests(): Single<List<User>>

    fun sendFollowRequest(userId: String): Single<String>

    fun cancelFollowRequest(userId: String): Single<String>

    fun unfollow(userId: String): Single<String>

    fun acceptRequest(userId: String): Single<String>

    fun declineRequest(userId: String): Single<String>
}
