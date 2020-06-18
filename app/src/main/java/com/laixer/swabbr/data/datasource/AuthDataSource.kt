package com.laixer.swabbr.data.datasource

import com.laixer.swabbr.domain.model.AuthUser
import com.laixer.swabbr.domain.model.Login
import com.laixer.swabbr.domain.model.Registration
import com.laixer.swabbr.domain.model.Settings
import com.laixer.swabbr.domain.model.User
import io.reactivex.Completable
import io.reactivex.Single

interface AuthCacheDataSource {

    val key: String
        get() = "AUTH"

    fun set(authUser: AuthUser, remember: Boolean = true): Single<AuthUser>

    fun get(): Single<AuthUser>

    fun logout(): Completable
}

interface AuthRemoteDataSource {

    fun login(login: Login): Single<AuthUser>

    fun register(registration: Registration): Single<AuthUser>

    fun logout(): Completable

    fun getAuthenticatedUser(): Single<User>

    fun getSettings(): Single<Settings>

    fun saveSettings(settings: Settings): Single<Settings>
}
