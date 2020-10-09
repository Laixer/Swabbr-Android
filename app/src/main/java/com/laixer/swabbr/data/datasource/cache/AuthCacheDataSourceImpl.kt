package com.laixer.swabbr.data.datasource.cache

import com.laixer.cache.Cache
import com.laixer.swabbr.data.datasource.AuthCacheDataSource
import com.laixer.swabbr.domain.model.AuthUser
import io.reactivex.Completable
import io.reactivex.Single

class AuthCacheDataSourceImpl constructor(
    private val cache: Cache
) : AuthCacheDataSource {

    override fun set(authUser: AuthUser): Single<AuthUser> = cache.save(key, authUser)

    override fun get(): Single<AuthUser> = cache.load(key)

    override fun logout(): Completable = cache.delete(key)
}
