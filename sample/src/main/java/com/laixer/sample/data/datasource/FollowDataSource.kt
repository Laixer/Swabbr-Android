package com.laixer.sample.data.datasource

import com.laixer.sample.domain.model.FollowRequest
import com.laixer.sample.domain.model.User
import io.reactivex.Single

interface FollowCacheDataSource {

    fun get(receiverId: String): Single<FollowRequest>

    fun set(followRequest: FollowRequest): Single<FollowRequest>

    fun get(userId: String): Single<List<FollowRequest>>

    fun set(followRequest: List<FollowRequest>): Single<List<FollowRequest>>

}

interface FollowRemoteDataSource {

    fun get(receiverId: String): Single<FollowRequest>

    fun set(followRequest: FollowRequest): Single<FollowRequest>

    fun getFollow(userId: String): Single<List<FollowRequest>>

}
