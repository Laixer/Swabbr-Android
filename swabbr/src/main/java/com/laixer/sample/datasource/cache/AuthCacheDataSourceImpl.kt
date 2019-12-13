package com.laixer.swabbr.datasource.cache

import com.laixer.cache.ReactiveCache
import com.laixer.swabbr.data.datasource.AuthCacheDataSource
import com.laixer.swabbr.data.datasource.ReactionCacheDataSource
import com.laixer.swabbr.domain.model.Reaction
import com.laixer.swabbr.domain.model.Settings
import com.laixer.swabbr.domain.model.User
import io.reactivex.Single

class AuthCacheDataSourceImpl constructor(
    private val cache: ReactiveCache<String>
) : AuthCacheDataSource {

    val key = "Access token"

    override fun login(token: String) {
        cache.save(key, token)
    }

    override fun register(token: String) {
        cache.save(key, token)
    }
}
