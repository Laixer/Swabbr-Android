package com.laixer.swabbr.data.datasource.cache

import com.laixer.cache.ReactiveCache
import com.laixer.swabbr.data.datasource.UserCacheDataSource
import com.laixer.swabbr.domain.model.User
import io.reactivex.Single
import java.util.*

class UserCacheDataSourceImpl constructor(
    private val cache: ReactiveCache<User>
) : UserCacheDataSource {

    override fun set(list: List<User>): Single<List<User>> {
        list.map { set(it) }
        return getAll()
    }

    override fun getAll(): Single<List<User>> = cache.loadAll()

    override fun get(userId: UUID): Single<User> = cache.load(userId.toString())

    override fun getSelf(): Single<User> = cache.load(self_key)

    override fun set(item: User): Single<User> = cache.save(item.id.toString(), item)
}

