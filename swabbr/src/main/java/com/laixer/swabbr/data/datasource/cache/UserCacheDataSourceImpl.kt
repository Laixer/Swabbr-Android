package com.laixer.swabbr.data.datasource.cache

import com.laixer.cache.ReactiveCache
import com.laixer.swabbr.data.datasource.UserCacheDataSource
import com.laixer.swabbr.domain.model.User
import io.reactivex.Single
import java.util.UUID

class UserCacheDataSourceImpl constructor(
    private val cache: ReactiveCache<List<User>>
) : UserCacheDataSource {

    override fun set(list: List<User>): Single<List<User>> = cache.save(key, list)

    override fun get(userId: UUID): Single<User> = cache.load(key).map { list -> list.first { it.id == userId } }

    override fun set(item: User): Single<User> = cache.load(key).onErrorResumeNext { set(listOf(item)) }
        .map { list -> list.filter { it.id != item.id }.plus(item) }.flatMap { set(it) }.map { item }
}
