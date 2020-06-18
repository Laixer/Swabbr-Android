package com.laixer.swabbr.data.repository

import com.laixer.swabbr.data.datasource.AuthCacheDataSource
import com.laixer.swabbr.data.datasource.AuthRemoteDataSource
import com.laixer.swabbr.domain.model.AuthUser
import com.laixer.swabbr.domain.model.Login
import com.laixer.swabbr.domain.model.Registration
import com.laixer.swabbr.domain.model.Settings
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
            .flatMap { authCacheDataSource.set(it) }
        false -> authCacheDataSource.get().onErrorResumeNext(getAuthenticatedUser(true))
    }

    override fun login(login: Login, remember: Boolean): Single<AuthUser> =
        authRemoteDataSource.login(login)
            .flatMap { authCacheDataSource.set(it, remember) }

    override fun register(registration: Registration, remember: Boolean): Single<AuthUser> =
        authRemoteDataSource.register(registration)
            .flatMap { authCacheDataSource.set(it, remember) }

    override fun logout(): Completable = authRemoteDataSource.logout().andThen(authCacheDataSource.logout())

    override fun getSettings(refresh: Boolean): Single<Settings> = when (refresh) {
        true -> authRemoteDataSource.getSettings()
            .flatMap { result -> authCacheDataSource.get().map { it.apply { userSettings = result } } }
            .flatMap { authCacheDataSource.set(it) }.map { it.userSettings }
        false -> authCacheDataSource.get().map { it.userSettings }.onErrorResumeNext { getSettings(true) }
    }

    override fun saveSettings(settings: Settings): Single<Settings> =
        authRemoteDataSource.saveSettings(settings)
            .flatMap { result -> authCacheDataSource.get().map { it.apply { userSettings = result } } }
            .flatMap { authCacheDataSource.set(it) }
            .map { it.userSettings }
}
