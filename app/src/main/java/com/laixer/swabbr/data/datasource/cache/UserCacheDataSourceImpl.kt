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

    override fun set(list: List<User>): Single<List<User>> = cache.load<HashMap<UUID, User>>(key)
        .flatMap { map ->
            map.putAll(list.map { it.id to it }.toMap())
            cache.save(key, map)
        }.map { it.values.toList() }

    override fun get(): Single<List<User>> = cache.load<HashMap<UUID, User>>(key).map { it.values.toList() }

    override fun get(userId: UUID): Single<User> = cache.load<HashMap<UUID, User>>(key)
        .map { map -> map[userId] }

    override fun add(user: User): Single<User> =  cache.load<HashMap<UUID, User>>(key)
        .map { it.put(user.id, user) }
}

