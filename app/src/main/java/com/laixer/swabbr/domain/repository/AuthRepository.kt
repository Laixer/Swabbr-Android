package com.laixer.swabbr.domain.repository

import com.laixer.swabbr.domain.model.*
import io.reactivex.Completable
import io.reactivex.Single

interface AuthRepository {

    fun getAuthenticatedUser(refresh: Boolean): Single<AuthUser>

    fun updateAuthenticatedUser(item: User): Single<AuthUser>

    fun login(login: Login): Single<AuthUser>

    fun register(registration: Registration): Single<AuthUser>

    fun logout(): Completable

    fun getSettings(refresh: Boolean): Single<Settings>

    fun saveSettings(settings: Settings): Single<Settings>

    fun getStatistics(refresh: Boolean): Single<UserStatistics>

    fun getIncomingFollowRequests(): Single<List<FollowRequest>>
}
