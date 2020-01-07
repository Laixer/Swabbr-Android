package com.laixer.swabbr.data.repository

import com.laixer.swabbr.data.datasource.AuthCacheDataSource
import com.laixer.swabbr.data.datasource.AuthRemoteDataSource
import com.laixer.swabbr.data.datasource.SettingsCacheDataSource
import com.laixer.swabbr.data.datasource.UserCacheDataSource
import com.laixer.swabbr.domain.model.Login
import com.laixer.swabbr.domain.model.Registration
import com.laixer.swabbr.domain.model.Settings
import com.laixer.swabbr.domain.model.User
import com.laixer.swabbr.domain.repository.AuthRepository
import io.reactivex.Single
import io.reactivex.functions.Function3

class AuthRepositoryImpl constructor(
    private val authCacheDataSource: AuthCacheDataSource,
    private val userCacheDataSource: UserCacheDataSource,
    private val settingsCacheDataSource: SettingsCacheDataSource,
    private val remoteDataSource: AuthRemoteDataSource
) : AuthRepository {

    override fun login(login: Login): Single<Pair<Pair<String, User>, Settings>> =
        remoteDataSource.login(login)
            .flatMap {
                Single.zip(
                    authCacheDataSource.set(Pair(it.first, it.second)),
                    userCacheDataSource.set(it.second),
                    settingsCacheDataSource.set(it.third),
                    Function3<Pair<String, User>, User, Settings, Pair<Pair<String, User>, Settings>>
                    { authUser, _, settings ->
                        Pair(
                            authUser,
                            settings
                        )
                    }
                )
            }

    override fun register(registration: Registration): Single<Pair<Pair<String, User>, Settings>> =
        remoteDataSource.register(registration)
            .flatMap {
                Single.zip(
                    authCacheDataSource.set(Pair(it.first, it.second)),
                    userCacheDataSource.set(it.second),
                    settingsCacheDataSource.set(it.third),
                    Function3<Pair<String, User>, User, Settings, Pair<Pair<String, User>, Settings>>
                    { authUser, _, settings ->
                        Pair(
                            authUser,
                            settings
                        )
                    }
                )
            }

    override fun getToken(): Single<String> = authCacheDataSource.getToken()
}
