package com.laixer.swabbr.data.datasource

import com.laixer.swabbr.data.api.AuthApi
import com.laixer.swabbr.data.model.mapToData
import com.laixer.swabbr.data.model.mapToDomain
import com.laixer.swabbr.data.interfaces.AuthDataSource
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

    override fun delete(): Completable = authApi.delete()
}
