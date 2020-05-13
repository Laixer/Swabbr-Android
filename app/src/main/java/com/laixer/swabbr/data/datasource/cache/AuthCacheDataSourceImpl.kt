package com.laixer.swabbr.data.datasource.cache

import com.laixer.cache.MemoryCache
import com.laixer.cache.ReactiveCache
import com.laixer.swabbr.data.datasource.AuthCacheDataSource
import com.laixer.swabbr.domain.model.AuthUser
import io.reactivex.Completable
import io.reactivex.Single

class AuthCacheDataSourceImpl constructor(
    private val cache: ReactiveCache<AuthUser>,
    private val memory: MemoryCache<AuthUser>
) : AuthCacheDataSource {

    override fun set(authUser: AuthUser, remember: Boolean): Single<AuthUser> =
        if (remember) {
            cache.save(key, memory.save(key, authUser))
        } else {
            Single.just(memory.save(key, authUser))
        }

    override fun get(): Single<AuthUser> = try {
        Single.just(memory.load(key))
    } catch (e: NoSuchElementException) {
        cache.load(key)
    }

    override fun logout(): Completable = cache.delete(key).doOnComplete { memory.delete(key) }

}
