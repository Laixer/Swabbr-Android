package com.laixer.swabbr.data.repository

import com.laixer.swabbr.data.interfaces.AuthCacheDataSource
import com.laixer.swabbr.data.interfaces.AuthDataSource
import com.laixer.swabbr.domain.interfaces.AuthRepository
import com.laixer.swabbr.domain.model.Login
import com.laixer.swabbr.domain.model.Registration
import com.laixer.swabbr.domain.model.TokenWrapper
import io.reactivex.Completable
import io.reactivex.Single

/**
 *  Repository for user authentication.
 */
class AuthRepositoryImpl constructor(
    private val cacheDataSource: AuthCacheDataSource,
    private val remoteDataSource: AuthDataSource
) : AuthRepository {

    override fun login(login: Login): Single<TokenWrapper> = remoteDataSource.login(login)
        .flatMap(cacheDataSource::set)

    override fun register(registration: Registration): Completable = remoteDataSource.register(registration)

    override fun logout(): Completable = remoteDataSource.logout()
}
