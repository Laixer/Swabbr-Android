package com.laixer.swabbr.domain.repository

import com.laixer.swabbr.domain.model.AuthUser
import com.laixer.swabbr.domain.model.Login
import com.laixer.swabbr.domain.model.Registration
import io.reactivex.Completable
import io.reactivex.Single

interface AuthRepository {

    fun getAuthenticatedUser(): Single<AuthUser>

    fun login(login: Login, remember: Boolean = true): Single<AuthUser>

    fun register(registration: Registration, remember: Boolean = true): Single<AuthUser>

    fun logout(): Completable
}
