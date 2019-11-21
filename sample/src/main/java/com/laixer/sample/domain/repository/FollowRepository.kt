package com.laixer.sample.domain.repository

import com.laixer.sample.domain.model.FollowRequest
import io.reactivex.Single

interface FollowRepository {

    fun get(receiverId: String): Single<List<FollowRequest>>

    fun get(requesterId: String, receiverId: String): Single<FollowRequest>
}
