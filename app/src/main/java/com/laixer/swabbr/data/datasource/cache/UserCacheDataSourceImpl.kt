package com.laixer.swabbr.data.datasource.cache

import com.laixer.cache.Cache
import com.laixer.swabbr.data.datasource.UserCacheDataSource
import com.laixer.swabbr.domain.model.User
import com.laixer.swabbr.domain.model.UserComplete
import com.laixer.swabbr.domain.model.UserWithStats
import io.reactivex.Single
import java.util.*

/**
 *  Caching for user objects.
 */
class UserCacheDataSourceImpl constructor(
    private val cache: Cache
) : UserCacheDataSource {

    override fun add(user: User): Single<User> = cache
        .load<List<User>>(key)
        .onErrorResumeNext { Single.just(emptyList()) }
        .map { list -> list.filter { it.id !== user.id }.toMutableList().apply { add(user) } }
        .map { list -> set(list) }
        .map { user }

    override fun addWithStats(user: UserWithStats): Single<UserWithStats> = cache
        .load<List<UserWithStats>>(keyWithStats)
        .onErrorResumeNext { Single.just(emptyList()) }
        .map { list -> list.filter { it.id !== user.id }.toMutableList().apply { addWithStats(user) } }
        .map { list -> setWithStats(list) }
        .map { user }

    override fun get(userId: UUID): Single<User> = cache
        .load<List<User>>(key)
        .map { list -> list.first { it.id == userId } }

    override fun get(): Single<List<User>> = cache.load(key)

    override fun getWithStats(userId: UUID): Single<UserWithStats> = cache.load(keyWithStats)

    override fun getSelf(): Single<UserComplete> = cache.load(keySelf)

    override fun getSelfWithStats(): Single<UserWithStats> = cache.load(keySelfWithStats)

    override fun set(list: List<User>): Single<List<User>> = cache.save(key, list)

    override fun setWithStats(list: List<UserWithStats>): Single<List<UserWithStats>> = cache.save(keyWithStats, list)

    override fun setSelf(user: UserComplete): Single<UserComplete> = cache.save(keySelf, user)

    override fun setSelfWithStats(user: UserWithStats): Single<UserWithStats> = cache.save(keySelfWithStats, user)

    override fun getFollowers(userId: UUID): Single<List<User>> = cache.load(generateFollowersKey(userId))

    override fun setFollowers(userId: UUID, users: List<User>): Single<List<User>> =
        cache.save(generateFollowersKey(userId), users)

    override fun getFollowing(userId: UUID): Single<List<User>> = cache.load(generateFollowingKey(userId))

    override fun setFollowing(userId: UUID, users: List<User>): Single<List<User>> =
        cache.save(generateFollowingKey(userId), users)
}
