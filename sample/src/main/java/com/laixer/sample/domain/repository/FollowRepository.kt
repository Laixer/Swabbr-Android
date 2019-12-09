package com.laixer.sample.domain.repository

import com.laixer.sample.domain.model.User
import io.reactivex.Single

interface FollowRepository {

    fun getFollowStatus(targetId: String): Single<String>

    fun getFollowers(targetId: String): Single<List<User>>

    fun getFollowing(targetId: String): Single<List<User>>

    fun getIncomingRequests(): Single<List<User>>

    fun sendFollowRequest()
}
