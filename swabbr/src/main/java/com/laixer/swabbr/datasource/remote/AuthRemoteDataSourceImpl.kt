package com.laixer.swabbr.datasource.remote

import com.laixer.swabbr.data.datasource.AuthRemoteDataSource
import com.laixer.swabbr.datasource.model.mapToData
import com.laixer.swabbr.datasource.model.mapToDomain
import com.laixer.swabbr.domain.model.AuthUser
import com.laixer.swabbr.domain.model.Login
import com.laixer.swabbr.domain.model.Registration
import io.reactivex.Completable
import io.reactivex.Single

class AuthRemoteDataSourceImpl constructor(
    private val api: AuthApi
) : AuthRemoteDataSource {

    override fun login(login: Login): Single<AuthUser> =
        api.login(login.mapToData())
            .map {
                AuthUser(
                    it.accessToken,
                    it.user.mapToDomain(),
                    it.userSettings.mapToDomain()
                )
            }

    override fun register(registration: Registration): Single<AuthUser> =
        api.register(registration.mapToData())
            .map {
                AuthUser(
                    it.accessToken,
                    it.user.mapToDomain(),
                    it.userSettings.mapToDomain()
                )
            }

    override fun logout(): Completable = api.logout()
}
