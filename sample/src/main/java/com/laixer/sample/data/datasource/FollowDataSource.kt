package com.laixer.sample.data.datasource

import com.laixer.sample.domain.model.FollowRequest
import com.laixer.sample.domain.model.User
import io.reactivex.Single

interface FollowCacheDataSource {

    fun get(receiverId: String): Single<List<FollowRequest>>

    fun set(followRequests: List<FollowRequest>): Single<List<FollowRequest>>

    fun get(requesterId: String, receiverId: String): Single<FollowRequest>

    fun set(followRequest: FollowRequest): Single<FollowRequest>

}

interface FollowRemoteDataSource {

    fun get(receiverId: String): Single<List<FollowRequest>>

    fun get(requesterId: String, receiverId: String): Single<FollowRequest>

    fun set(followRequest: FollowRequest): Single<FollowRequest>

}
