package com.laixer.swabbr.data.datasource.remote

import com.laixer.swabbr.data.datasource.AuthRemoteDataSource
import com.laixer.swabbr.data.datasource.model.mapToData
import com.laixer.swabbr.data.datasource.model.mapToDomain
import com.laixer.swabbr.data.datasource.model.remote.AuthApi
import com.laixer.swabbr.data.datasource.model.remote.FollowApi
import com.laixer.swabbr.data.datasource.model.remote.SettingsApi
import com.laixer.swabbr.data.datasource.model.remote.UsersApi
import com.laixer.swabbr.domain.model.*
import io.reactivex.Completable
import io.reactivex.Single

class AuthRemoteDataSourceImpl constructor(
    private val authApi: AuthApi,
    private val settingsApi: SettingsApi,
    private val usersApi: UsersApi,
    private val followApi: FollowApi
) : AuthRemoteDataSource {

    override fun login(login: Login): Single<AuthUser> = authApi.login(login.mapToData()).map { it.mapToDomain() }

    override fun register(registration: Registration): Single<AuthUser> =
        authApi.register(registration.mapToData()).map { it.mapToDomain() }

    override fun logout(): Completable = authApi.logout()

    override fun getAuthenticatedUser(): Single<User> = authApi.self().map { it.mapToDomain() }

    override fun updateAuthenticatedUser(user: User): Single<User> = usersApi.update(user.mapToData()).map { it.mapToDomain() }

    override fun getSettings(): Single<Settings> = settingsApi.get()
        .map { it.mapToDomain() }

    override fun saveSettings(settings: Settings): Single<Settings> = settingsApi.set(settings.mapToData())
        .map { it.mapToDomain() }

    override fun getStatistics(): Single<UserStatistics> = usersApi.getSelfStatistics().map { it.mapToDomain() }

    override fun getIncomingFollowRequests(): Single<List<FollowRequest>> =
        followApi.getIncomingRequests().map { it.followRequests.mapToDomain() }
}
