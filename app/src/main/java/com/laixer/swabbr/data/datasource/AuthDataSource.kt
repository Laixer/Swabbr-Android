package com.laixer.swabbr.data.datasource

import com.laixer.swabbr.domain.model.*
import io.reactivex.Completable
import io.reactivex.Single

interface AuthCacheDataSource {

    val key: String
        get() = "AUTH"

    fun set(authUser: AuthUser): Single<AuthUser>

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

    fun getStatistics(): Single<UserStatistics>

    fun getIncomingFollowRequests(): Single<List<FollowRequest>>
}
