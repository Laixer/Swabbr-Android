package com.laixer.swabbr.datasource.cache

import com.laixer.cache.ReactiveCache
import com.laixer.swabbr.data.datasource.AuthCacheDataSource
import io.reactivex.Single

class AuthCacheDataSourceImpl constructor(
    private val cache: ReactiveCache<Pair<String, String>>
) : AuthCacheDataSource {

    val key = "Authorized user"

    // First is Token, second is UserId
    override fun set(authorizedUser: Pair<String, String>): Single<Pair<String, String>> = cache.save(key, authorizedUser)

    override fun get(): Single<Pair<String, String>> =
        cache.load(key)

    override fun logout() {
        cache.delete(key)
    }

    override fun getToken(): Single<String> = cache.load(key).flatMap { Single.just(it.first) }

    override fun getUserId(): Single<String> = cache.load(key).flatMap { Single.just(it.second) }
}
