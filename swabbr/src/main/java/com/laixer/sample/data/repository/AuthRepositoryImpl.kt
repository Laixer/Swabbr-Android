package com.laixer.swabbr.data.repository

import com.laixer.swabbr.data.datasource.AuthCacheDataSource
import com.laixer.swabbr.data.datasource.AuthRemoteDataSource
import com.laixer.swabbr.data.datasource.SettingsCacheDataSource
import com.laixer.swabbr.data.datasource.UserCacheDataSource
import com.laixer.swabbr.domain.model.Login
import com.laixer.swabbr.domain.model.Registration
import com.laixer.swabbr.domain.repository.AuthRepository

class AuthRepositoryImpl constructor(
    private val authCacheDataSource: AuthCacheDataSource,
    private val userCacheDataSource: UserCacheDataSource,
    private val settingsCacheDataSource: SettingsCacheDataSource,
    private val remoteDataSource: AuthRemoteDataSource
) : AuthRepository {

    override fun login(login: Login) {
        remoteDataSource.login(login).flatMap {
            authCacheDataSource.login(it.first)
            userCacheDataSource.set(it.second)
            settingsCacheDataSource.set(it.third)
        }
    }

    override fun register(registration: Registration) {
        remoteDataSource.register(registration).flatMap {
            authCacheDataSource.login(it.first)
            userCacheDataSource.set(it.second)
            settingsCacheDataSource.set(it.third)
        }
    }
}
