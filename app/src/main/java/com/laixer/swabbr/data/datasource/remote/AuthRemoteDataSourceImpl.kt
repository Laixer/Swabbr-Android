package com.laixer.swabbr.data.datasource.remote

import com.laixer.swabbr.data.datasource.AuthDataSource
import com.laixer.swabbr.data.datasource.model.mapToData
import com.laixer.swabbr.data.datasource.model.mapToDomain
import com.laixer.swabbr.data.datasource.model.remote.AuthApi
import com.laixer.swabbr.domain.model.Login
import com.laixer.swabbr.domain.model.Registration
import com.laixer.swabbr.domain.model.TokenWrapper
import io.reactivex.Completable
import io.reactivex.Single

class AuthRemoteDataSourceImpl constructor(
    private val authApi: AuthApi
) : AuthDataSource {

    override fun login(login: Login): Single<TokenWrapper> =
        authApi.login(login.mapToData()).map { it.mapToDomain() }

    override fun register(registration: Registration): Completable =
        authApi.register(registration.mapToData())

    override fun logout(): Completable = authApi.logout()
}
