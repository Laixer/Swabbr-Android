package com.laixer.swabbr.data.datasource.cache

import com.laixer.cache.Cache
import com.laixer.swabbr.data.datasource.AuthCacheDataSource
import com.laixer.swabbr.domain.model.TokenWrapper
import io.reactivex.Completable
import io.reactivex.Single

/**
 *  Caching for authentication tokens.
 */
class AuthCacheDataSourceImpl constructor(
    private val cache: Cache
) : AuthCacheDataSource {

    override fun set(tokenWrapper: TokenWrapper): Single<TokenWrapper> = cache.save(key, tokenWrapper)

    override fun get(): Single<TokenWrapper> = cache.load(key)

    override fun logout(): Completable = cache.delete(key)
}
