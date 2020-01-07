package com.laixer.swabbr.datasource.remote

import com.laixer.swabbr.data.datasource.AuthRemoteDataSource
import com.laixer.swabbr.datasource.model.mapToData
import com.laixer.swabbr.datasource.model.mapToDomain
import com.laixer.swabbr.domain.model.Login
import com.laixer.swabbr.domain.model.Registration
import com.laixer.swabbr.domain.model.Settings
import com.laixer.swabbr.domain.model.User
import io.reactivex.Single

class AuthRemoteDataSourceImpl constructor(
    private val api: AuthApi
) : AuthRemoteDataSource {

    override fun login(login: Login): Single<Triple<String, User, Settings>> =
        api.login(login.mapToData())
            .map {
                Triple(
                    it.accessToken,
                    it.user.mapToDomain(),
                    it.userSettings.mapToDomain()
                )
            }

    override fun register(registration: Registration): Single<Triple<String, User, Settings>> =
        api.register(registration.mapToData())
            .map {
                Triple(
                    it.accessToken,
                    it.user.mapToDomain(),
                    it.userSettings.mapToDomain()
                )
            }
}
