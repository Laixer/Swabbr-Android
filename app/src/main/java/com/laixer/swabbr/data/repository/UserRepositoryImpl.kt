package com.laixer.swabbr.data.repository

import com.laixer.swabbr.data.datasource.UserCacheDataSource
import com.laixer.swabbr.data.datasource.UserDataSource
import com.laixer.swabbr.domain.model.User
import com.laixer.swabbr.domain.model.UserComplete
import com.laixer.swabbr.domain.model.UserWithStats
import com.laixer.swabbr.domain.repository.UserRepository
import io.reactivex.Completable
import io.reactivex.Single
import java.util.*

// TODO Implement forceRefresh functionality fully
/**
 *  Repository for user objects. Note that this is also capable of
 *  retrieving user statistics and user personal details.
 */
class UserRepositoryImpl constructor(
    private val cacheDataSource: UserCacheDataSource,
    private val remoteDataSource: UserDataSource
) : UserRepository {

    override fun get(userId: UUID, forceRefresh: Boolean): Single<User> = when (forceRefresh) {
        true -> remoteDataSource.get(userId).flatMap { cacheDataSource.add(it) }
        false -> cacheDataSource.get(userId).onErrorResumeNext { get(userId, true) }
    }

    override fun getWithStats(userId: UUID, forceRefresh: Boolean): Single<UserWithStats> = when (forceRefresh) {
        true -> remoteDataSource.getWithStats(userId).flatMap { cacheDataSource.addWithStats(it) }
        false -> cacheDataSource.getWithStats(userId).onErrorResumeNext { getWithStats(userId, true) }
    }

    override fun getSelf(forceRefresh: Boolean): Single<UserComplete> = when (forceRefresh) {
        true -> remoteDataSource.getSelf().flatMap { cacheDataSource.setSelf(it) }
        false -> cacheDataSource.getSelf().onErrorResumeNext { getSelf(true) }
    }

    override fun getSelfWithStats(forceRefresh: Boolean): Single<UserWithStats> = when (forceRefresh) {
        true -> remoteDataSource.getSelfWithStats().flatMap { cacheDataSource.setSelfWithStats(it) }
        false -> cacheDataSource.getSelfWithStats().onErrorResumeNext { getSelfWithStats(true) }
    }

    override fun getFollowing(userId: UUID, forceRefresh: Boolean): Single<List<User>> =
        remoteDataSource.getFollowing(userId)

    override fun getFollowers(userId: UUID, forceRefresh: Boolean): Single<List<User>> =
        remoteDataSource.getFollowers(userId)

    override fun search(query: String, offset: Int, limit: Int): Single<List<User>> =
        remoteDataSource.search(query, offset, limit)

    override fun update(user: UserComplete): Completable = remoteDataSource.update(user)
}
