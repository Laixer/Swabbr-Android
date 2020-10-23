package com.laixer.swabbr.data.repository

import com.laixer.swabbr.data.datasource.AuthCacheDataSource
import com.laixer.swabbr.data.datasource.AuthRemoteDataSource
import com.laixer.swabbr.domain.model.*
import com.laixer.swabbr.domain.repository.AuthRepository
import io.reactivex.Completable
import io.reactivex.Single

class AuthRepositoryImpl constructor(
    private val authCacheDataSource: AuthCacheDataSource,
    private val authRemoteDataSource: AuthRemoteDataSource
) : AuthRepository {

    override fun getAuthenticatedUser(refresh: Boolean): Single<AuthUser> = when (refresh) {
        true -> authRemoteDataSource.getAuthenticatedUser()
            .flatMap { user -> authCacheDataSource.get().map { it.apply { it.user = user } } }
            .flatMap(authCacheDataSource::set)
        false -> authCacheDataSource.get()
    }

    override fun login(login: Login): Single<AuthUser> = authRemoteDataSource.login(login)
        .flatMap(authCacheDataSource::set)

    override fun register(registration: Registration): Single<AuthUser> = authRemoteDataSource.register(registration)
        .flatMap(authCacheDataSource::set)


    override fun logout(): Completable = authRemoteDataSource.logout()

    override fun getSettings(refresh: Boolean): Single<Settings> = when (refresh) {
        true -> authRemoteDataSource.getSettings()
            .flatMap { result -> authCacheDataSource.get().map { it.apply { userSettings = result } } }
            .flatMap(authCacheDataSource::set).map { it.userSettings }
        false -> authCacheDataSource.get()
            .map { it.userSettings ?: throw NullPointerException() }
            .onErrorResumeNext { getSettings(true) }
    }

    override fun saveSettings(settings: Settings): Single<Settings> =
        authRemoteDataSource.saveSettings(settings)
            .flatMap { result -> authCacheDataSource.get().map { it.apply { userSettings = result } } }
            .flatMap(authCacheDataSource::set)
            .map { it.userSettings }

    override fun getStatistics(refresh: Boolean): Single<UserStatistics> = authRemoteDataSource.getStatistics() // TODO: Implement caching

    override fun getIncomingFollowRequests(): Single<List<FollowRequest>> = authRemoteDataSource.getIncomingFollowRequests()

}
