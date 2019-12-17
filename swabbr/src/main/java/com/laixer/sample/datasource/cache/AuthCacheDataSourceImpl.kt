package com.laixer.swabbr.datasource.cache

import com.laixer.cache.ReactiveCache
import com.laixer.swabbr.data.datasource.AuthCacheDataSource
import com.laixer.swabbr.domain.model.User
import io.reactivex.Single

class AuthCacheDataSourceImpl constructor(
    private val cache: ReactiveCache<Pair<String, User>>
) : AuthCacheDataSource {

    val key = "Authorized user"

    override fun set(authorizedUser: Pair<String, User>): Single<Pair<String, User>> = cache.save(key, authorizedUser)

}
