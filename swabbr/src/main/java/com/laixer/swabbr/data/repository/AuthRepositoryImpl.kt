package com.laixer.swabbr.data.repository

import com.laixer.swabbr.data.datasource.AuthCacheDataSource
import com.laixer.swabbr.data.datasource.AuthRemoteDataSource
import com.laixer.swabbr.domain.model.AuthUser
import com.laixer.swabbr.domain.model.Login
import com.laixer.swabbr.domain.model.Registration
import com.laixer.swabbr.domain.model.Settings
import com.laixer.swabbr.domain.model.User
import com.laixer.swabbr.domain.repository.AuthRepository
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.functions.Function3
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.schedulers.Schedulers

class AuthRepositoryImpl constructor(
    private val authCacheDataSource: AuthCacheDataSource,
    private val authRemoteDataSource: AuthRemoteDataSource
) : AuthRepository {

    override fun login(login: Login): Single<AuthUser> =
        authRemoteDataSource.login(login)
            .flatMap {
                Single.zip(
                    authCacheDataSource.set(Pair(it.accessToken, it.user.id)),
                    Single.just(it.user),
                    Single.just(it.userSettings),
                    Function3<Pair<String, String>, User, Settings, AuthUser>
                    { auth, user, settings ->
                        AuthUser(
                            auth.first,
                            user,
                            settings
                        )
                    }
                )
            }

    override fun register(registration: Registration): Single<AuthUser> =
        authRemoteDataSource.register(registration)
            .flatMap {
                Single.zip(
                    authCacheDataSource.set(Pair(it.accessToken, it.user.id)),
                    Single.just(it.user),
                    Single.just(it.userSettings),
                    Function3<Pair<String, String>, User, Settings, AuthUser>
                    { auth, user, settings ->
                        AuthUser(
                            auth.first,
                            user,
                            settings
                        )
                    }
                )
            }

    override fun logout(): Completable =
        authRemoteDataSource.logout()
            .also {
                it.subscribeOn(Schedulers.io())
                it.subscribeWith(object : DisposableCompletableObserver() {
                    override fun onError(e: Throwable) {
                        return
                    }

                    override fun onComplete() = authCacheDataSource.logout()

                })
            }
}
