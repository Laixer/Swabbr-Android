package com.laixer.swabbr.domain.repository

import com.laixer.swabbr.domain.model.AuthUser
import com.laixer.swabbr.domain.model.Login
import com.laixer.swabbr.domain.model.Registration
import com.laixer.swabbr.domain.model.Settings
import io.reactivex.Completable
import io.reactivex.Single

interface AuthRepository {

    fun getAuthenticatedUser(refresh: Boolean): Single<AuthUser>

    fun login(login: Login, remember: Boolean = true): Single<AuthUser>

    fun register(registration: Registration, remember: Boolean = true): Single<AuthUser>

    fun logout(): Completable

    fun getSettings(refresh: Boolean): Single<Settings>

    fun saveSettings(settings: Settings): Single<Settings>
}
