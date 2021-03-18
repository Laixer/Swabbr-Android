package com.laixer.swabbr.data.cache

import com.laixer.swabbr.utils.cache.Cache
import com.laixer.swabbr.data.interfaces.FollowRequestCacheDataSource
import com.laixer.swabbr.domain.model.FollowRequest
import io.reactivex.Single

/**
 *  Basic caching implementation for follow requests.
 */
class FollowRequestCacheDataSourceImpl constructor(
    private val cache: Cache
) : FollowRequestCacheDataSource {
    override fun getIncomingRequests(): Single<List<FollowRequest>> = cache.load(keyIncoming)

    override fun getOutgoingRequests(): Single<List<FollowRequest>> = cache.load(keyOutgoing)

    override fun setIncomingRequests(followRequests: List<FollowRequest>): Single<List<FollowRequest>> =
        cache.save(keyIncoming, followRequests)

    override fun setOutgoingRequests(followRequests: List<FollowRequest>): Single<List<FollowRequest>> =
        cache.save(keyIncoming, followRequests)
}
