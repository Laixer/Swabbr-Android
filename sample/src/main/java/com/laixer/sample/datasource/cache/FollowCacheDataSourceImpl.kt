package com.laixer.sample.datasource.cache

import com.laixer.cache.ReactiveCache
import com.laixer.sample.data.datasource.FollowCacheDataSource
import com.laixer.sample.data.datasource.UserCacheDataSource
import com.laixer.sample.domain.model.FollowRequest
import com.laixer.sample.domain.model.User
import io.reactivex.Single

class FollowCacheDataSourceImpl constructor(
    private val cache: ReactiveCache<List<FollowRequest>>
) : FollowCacheDataSource {

    val key = "FollowRequest List"

    override fun get(receiverId: String): Single<List<FollowRequest>> =
         cache.load(key + receiverId)

    override fun set(followRequests: List<FollowRequest>): Single<List<FollowRequest>> =
        cache.save(key, followRequests)

    override fun get(requesterId: String, receiverId: String): Single<FollowRequest> =
        cache.load(key)
            .map { list -> list.first { it.requesterId == requesterId && it.receiverId == receiverId } }

    override fun set(followRequest: FollowRequest): Single<FollowRequest> =
        cache.load(key)
            .map { list -> list.filter { it.requesterId != followRequest.requesterId && it.receiverId != followRequest.receiverId }.plus(followRequest) }
            .flatMap { set(it) }
            .map { followRequest }
}
