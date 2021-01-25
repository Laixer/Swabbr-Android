package com.laixer.swabbr.data.repository

import com.laixer.swabbr.data.datasource.AuthCacheDataSource
import com.laixer.swabbr.data.datasource.AuthDataSource
import com.laixer.swabbr.domain.model.*
import com.laixer.swabbr.domain.repository.AuthRepository
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
