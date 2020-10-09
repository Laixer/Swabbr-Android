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

    override fun set(list: HashMap<String, User>): Single<Set<User>> = cache.save(key, list)

    override fun get(): Single<Set<User>> = cache.load(key)

    override fun get(userId: UUID): Single<User> = cache.load<Set<User>>(key)
        .flatMap { set -> Single.just(set.find { it.id === userId }) }

    override fun add(item: User): Single<User> =  cache.load<MutableSet<User>>(key)
        .flatMap { it.add(item)) }
}

