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
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.schedulers.Schedulers

class AuthRepositoryImpl constructor(
    private val authCacheDataSource: AuthCacheDataSource,
    private val authRemoteDataSource: AuthRemoteDataSource
) : AuthRepository {

    override fun getAuthenticatedUser(): Single<AuthUser> = authCacheDataSource.get()

    override fun login(login: Login, remember: Boolean): Single<AuthUser> =
        authRemoteDataSource.login(login)
            .flatMap { authCacheDataSource.set(it, remember) }

    override fun register(registration: Registration, remember: Boolean): Single<AuthUser> =
        authRemoteDataSource.register(registration)
            .flatMap { authCacheDataSource.set(it, remember) }

    override fun logout(): Completable = authRemoteDataSource.logout().also {
        it.subscribeOn(Schedulers.io())
        it.subscribeWith(object : DisposableCompletableObserver() {
            override fun onError(e: Throwable) {
                return
            }

            override fun onComplete() = authCacheDataSource.logout()
        })
    }

    override fun getSettings(): Single<Settings> = authCacheDataSource.get()
        .map { it.userSettings }
        .onErrorResumeNext { authRemoteDataSource.getSettings() }

    override fun saveSettings(settings: Settings): Single<Settings> = authRemoteDataSource.saveSettings(settings).also {
        it.subscribeOn(Schedulers.io())
        it.subscribeWith(object : DisposableCompletableObserver() {
            override fun onComplete() {
                var settings = authCacheDataSource.get().map { it.apply { userSettings = settings } }
                authCacheDataSource.set()
            }

            override fun onError(e: Throwable) {
                return
            }
        })
    }
        .flatMap { authCacheDataSource.get() }
        .flatMap {
            authCacheDataSource.set(it.apply { userSettings = settings })
                .map { user -> user.userSettings }
        }
}

