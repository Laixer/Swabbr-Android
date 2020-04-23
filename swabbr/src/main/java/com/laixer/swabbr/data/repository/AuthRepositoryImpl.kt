package com.laixer.swabbr.data.repository

import com.laixer.swabbr.data.datasource.AuthCacheDataSource
import com.laixer.swabbr.data.datasource.AuthRemoteDataSource
import com.laixer.swabbr.domain.model.AuthUser
import com.laixer.swabbr.domain.model.Login
import com.laixer.swabbr.domain.model.Registration
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
        authRemoteDataSource.login(login).flatMap {
            authCacheDataSource.set(it, remember)
        }

    override fun register(registration: Registration, remember: Boolean): Single<AuthUser> =
        authRemoteDataSource.register(registration).flatMap {
            authCacheDataSource.set(it, remember)
        }

    override fun logout(): Completable = authRemoteDataSource.logout().also {
        it.subscribeOn(Schedulers.io())
        it.subscribeWith(object : DisposableCompletableObserver() {
            override fun onError(e: Throwable) {
                return
            }

            override fun onComplete() = authCacheDataSource.logout()
        })
    }
}
