package com.laixer.swabbr.data.datasource

import com.laixer.swabbr.domain.model.Login
import com.laixer.swabbr.domain.model.Registration
import com.laixer.swabbr.domain.model.Settings
import com.laixer.swabbr.domain.model.User
import io.reactivex.Single

interface AuthRemoteDataSource {

    fun login(login: Login): Single<Triple<String, User, Settings>>

    fun register(registration: Registration): Single<Triple<String, User, Settings>>
}

interface AuthCacheDataSource {
    fun set(authorizedUser: Pair<String, User>): Single<Pair<String, User>>

    fun get(): Single<Pair<String, User>>
}
