package com.laixer.swabbr.data.datasource.remote

import com.laixer.swabbr.data.datasource.AuthRemoteDataSource
import com.laixer.swabbr.datasource.model.mapToData
import com.laixer.swabbr.datasource.model.mapToDomain
import com.laixer.swabbr.datasource.model.remote.AuthApi
import com.laixer.swabbr.datasource.model.remote.SettingsApi
import com.laixer.swabbr.domain.model.AuthUser
import com.laixer.swabbr.domain.model.Login
import com.laixer.swabbr.domain.model.Registration
import com.laixer.swabbr.domain.model.Settings
import io.reactivex.Completable
import io.reactivex.Single

class AuthRemoteDataSourceImpl constructor(
    private val authApi: AuthApi,
    private val settingsApi: SettingsApi
) : AuthRemoteDataSource {

    override fun login(login: Login): Single<AuthUser> = authApi.login(login.mapToData()).map {
        AuthUser(
            it.accessToken,
            it.user.mapToDomain(),
            it.userSettings.mapToDomain()
        )
    }

    override fun register(registration: Registration): Single<AuthUser> = authApi.register(registration.mapToData()).map {
        AuthUser(
            it.accessToken,
            it.user.mapToDomain(),
            it.userSettings.mapToDomain()
        )
    }

    override fun logout(): Completable = authApi.logout()

    override fun getSettings(): Single<Settings> = settingsApi.get()
        .map { it.mapToDomain() }

    override fun saveSettings(settings: Settings): Completable = settingsApi.set(settings.mapToData())
}
