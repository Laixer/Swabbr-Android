package com.laixer.swabbr.data.repository

import com.laixer.swabbr.data.datasource.UserCacheDataSource
import com.laixer.swabbr.data.datasource.UserRemoteDataSource
import com.laixer.swabbr.domain.model.User
import com.laixer.swabbr.domain.repository.UserRepository
import io.reactivex.Single

class UserRepositoryImpl constructor(
    private val cacheDataSource: UserCacheDataSource,
    private val remoteDataSource: UserRemoteDataSource
) : UserRepository {

    override fun get(refresh: Boolean): Single<List<User>> =
        when (refresh) {
            true -> remoteDataSource.get()
                .flatMap { cacheDataSource.set(it) }
            false -> cacheDataSource.get()
                .onErrorResumeNext { get(true) }
        }

    override fun get(userId: String, refresh: Boolean): Single<User> =
        when (refresh) {
            true -> remoteDataSource.get(userId)
                .flatMap { cacheDataSource.set(it) }
            false -> cacheDataSource.get(userId)
                .onErrorResumeNext { get(userId, true) }
        }
}
