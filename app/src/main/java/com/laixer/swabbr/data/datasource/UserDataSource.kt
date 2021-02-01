package com.laixer.swabbr.data.datasource

import com.laixer.swabbr.domain.model.User
import com.laixer.swabbr.domain.model.UserComplete
import com.laixer.swabbr.domain.model.UserWithStats
import com.laixer.swabbr.domain.types.Pagination
import io.reactivex.Completable
import io.reactivex.Single
import java.util.*

/**
 *  Caching for user data.
 */
interface UserCacheDataSource {

    val key: String get() = "USERS"
    val keyWithStats: String get() = "USERS_WITH_STATS"
    val keySelf: String get() = "USER_SELF"
    val keySelfWithStats: String get() = "USER_SELF_WITH_STATS"

    fun add(user: User): Single<User>

    fun addWithStats(user: UserWithStats): Single<UserWithStats>

    fun get(userId: UUID): Single<User>

    fun getWithStats(userId: UUID): Single<UserWithStats>

    fun get(): Single<List<User>>

    fun getSelf(): Single<UserComplete>

    fun getSelfWithStats(): Single<UserWithStats>

    fun set(list: List<User>): Single<List<User>>

    fun setWithStats(list: List<UserWithStats>): Single<List<UserWithStats>>

    fun setSelf(user: UserComplete): Single<UserComplete>

    fun setSelfWithStats(user: UserWithStats): Single<UserWithStats>

    fun generateFollowingKey(userId: UUID) = "FOLLOWS_$userId"

    fun generateFollowersKey(userId: UUID) = "FOLLOWERS_$userId"

    fun getFollowers(userId: UUID): Single<List<User>>

    fun setFollowers(userId: UUID, users: List<User>): Single<List<User>>

    fun getFollowing(userId: UUID): Single<List<User>>

    fun setFollowing(userId: UUID, users: List<User>): Single<List<User>>
}

/**
 *  Data source for users.
 */
interface UserDataSource {

    fun get(userId: UUID): Single<User>

    fun getWithStats(userId: UUID): Single<UserWithStats>

    fun getSelf(): Single<UserComplete>

    fun getSelfWithStats(): Single<UserWithStats>

    fun getFollowing(userId: UUID, pagination: Pagination = Pagination.latest()): Single<List<User>>

    fun getFollowers(userId: UUID, pagination: Pagination = Pagination.latest()): Single<List<User>>

    fun search(query: String, pagination: Pagination = Pagination.latest()): Single<List<User>>

    fun update(user: UserComplete): Completable
}
