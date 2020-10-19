package com.laixer.swabbr.data.datasource.cache

import com.laixer.cache.Cache
import com.laixer.swabbr.data.datasource.UserCacheDataSource
import com.laixer.swabbr.domain.model.User
import io.reactivex.Observable
import io.reactivex.Single
import java.util.*

class UserCacheDataSourceImpl constructor(
    private val cache: Cache
) : UserCacheDataSource {

    override fun set(list: List<User>): Single<List<User>> = cache.save(key, list)

    override fun get(): Single<List<User>> = cache.load(key)

    override fun get(userId: UUID): Single<User> = cache.load<List<User>>(key).map { list -> list.first { it.id == userId} }

    override fun add(user: User): Single<User> =  cache.load<List<User>>(key).onErrorResumeNext { Single.just(emptyList()) }
        .map { list -> list.filter { it.id !== user.id }.toMutableList().apply { add(user) } }
        .map { list -> set(list) }
        .map { user }
}

