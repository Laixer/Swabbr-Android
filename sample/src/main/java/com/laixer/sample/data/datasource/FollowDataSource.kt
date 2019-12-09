package com.laixer.sample.data.datasource

import com.laixer.sample.domain.model.User
import io.reactivex.Single

interface FollowRemoteDataSource {

    fun getFollowStatus(targetId: String): Single<String>

    fun getFollowers(targetId: String): Single<List<User>>

    fun getFollowing(targetId: String): Single<List<User>>

    fun getIncomingRequests(): Single<List<User>>

    fun sendFollowRequest(targetId: String)

    fun cancelFollowRequest(targetId: String)

    fun unfollow(targetId: String)

    fun acceptRequest(targetId: String)

    fun declineRequest(targetId: String)
}
