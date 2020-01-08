package com.laixer.swabbr.data.datasource

import com.laixer.swabbr.domain.model.AuthUser
import com.laixer.swabbr.domain.model.Login
import com.laixer.swabbr.domain.model.Registration
import io.reactivex.Completable
import io.reactivex.Single

interface AuthRemoteDataSource {

    fun login(login: Login): Single<AuthUser>

    fun register(registration: Registration): Single<AuthUser>

    fun logout(token: String): Completable
}

interface AuthCacheDataSource {
    fun set(authorizedUser: Pair<String, String>): Single<Pair<String, String>>

    fun get(): Single<Pair<String, String>>

    fun logout()

    fun getToken(): Single<String>

    fun getUserId(): Single<String>
}
