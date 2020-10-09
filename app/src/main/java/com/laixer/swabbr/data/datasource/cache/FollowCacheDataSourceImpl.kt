package com.laixer.swabbr.data.datasource.cache

import com.laixer.cache.Cache
import com.laixer.swabbr.data.datasource.FollowCacheDataSource
import com.laixer.swabbr.domain.model.User
import io.reactivex.Single
import java.util.*

class FollowCacheDataSourceImpl constructor(
    private val cache: Cache

) : FollowCacheDataSource {
    override fun getFollowers(userId: UUID): Single<List<User>> = cache.load(getFollowersKey(userId))

    override fun setFollowers(userId: UUID, users: List<User>): Single<List<User>> = cache.save(getFollowersKey(userId), users)

    override fun getFollowing(userId: UUID): Single<List<User>> = cache.load(getFollowingKey(userId))

    override fun setFollowing(userId: UUID, users: List<User>): Single<List<User>> =
        cache.save(getFollowingKey(userId), users)
}
