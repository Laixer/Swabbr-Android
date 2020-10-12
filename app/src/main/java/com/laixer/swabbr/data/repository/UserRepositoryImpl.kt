package com.laixer.swabbr.data.repository

import com.laixer.swabbr.data.datasource.UserCacheDataSource
import com.laixer.swabbr.data.datasource.UserRemoteDataSource
import com.laixer.swabbr.domain.model.User
import com.laixer.swabbr.domain.repository.UserRepository
import io.reactivex.Single
import java.util.UUID

class UserRepositoryImpl constructor(
    private val cacheDataSource: UserCacheDataSource,
    private val remoteDataSource: UserRemoteDataSource
) : UserRepository {

    override fun get(userId: UUID, refresh: Boolean): Single<User> = when (refresh) {
        true -> remoteDataSource.get(userId).flatMap { cacheDataSource.add(it) }
        false -> cacheDataSource.get(userId).onErrorResumeNext { get(userId, true) }
    }

    override fun set(user: User): Single<User> = cacheDataSource.add(user)

    override fun search(name: String?, page: Int, itemsPerPage: Int): Single<List<User>> =
        remoteDataSource.search(name, page, itemsPerPage).flatMap { cacheDataSource.set(it) }

}
