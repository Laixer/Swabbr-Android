package com.laixer.sample.datasource.cache

import com.laixer.cache.ReactiveCache
import com.laixer.sample.data.datasource.FollowCacheDataSource
import com.laixer.sample.data.datasource.UserCacheDataSource
import com.laixer.sample.domain.model.FollowRequest
import com.laixer.sample.domain.model.User
import io.reactivex.Single

class FollowCacheDataSourceImpl constructor(
    private val cache: ReactiveCache<FollowRequest>
) : FollowCacheDataSource {

    val key = "FollowRequest List"

    override fun get(receiverId: String): Single<FollowRequest> =
        cache.load(key + receiverId)

    override fun set(followRequest: FollowRequest): Single<FollowRequest> =
        cache.save(key + followRequest.receiverId, followRequest)

    override fun get(userId: String): Single<List<FollowRequest>> =
        cache.load(key, userId)
    }

    override fun set(followRequest: List<FollowRequest>): Single<List<FollowRequest>> =
        //
    }
}
