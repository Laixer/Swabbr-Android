package com.laixer.sample.domain.repository

import com.laixer.sample.domain.model.FollowRequest
import io.reactivex.Single

interface FollowRepository {

    fun get(userId: String): Single<List<FollowRequest>>

    fun get(receiverId: String): Single<FollowRequest>

    fun requestFollow(receiverId: String): Single<FollowRequest>

    fun requestUnfollow(receiverId: String): Single<FollowRequest>
}
