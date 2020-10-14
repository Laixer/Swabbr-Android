package com.laixer.swabbr.domain.usecase

import com.laixer.swabbr.domain.model.AuthUser
import com.laixer.swabbr.domain.model.Login
import com.laixer.swabbr.domain.model.Registration
import com.laixer.swabbr.domain.repository.AuthRepository
import io.reactivex.Completable
import io.reactivex.Single

class AuthUseCase constructor(
    private val authRepository: AuthRepository
) {

    fun getAuthenticatedUser(refresh: Boolean): Single<AuthUser> = authRepository.getAuthenticatedUser(refresh)

    fun login(login: Login): Single<AuthUser> = authRepository.login(login)

    fun register(registration: Registration): Single<AuthUser> =
        authRepository.register(registration)

    fun logout(): Completable = authRepository.logout()
}
