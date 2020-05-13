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

    fun getAuthenticatedUser(): Single<AuthUser> = authRepository.getAuthenticatedUser()

    fun login(login: Login, remember: Boolean = true): Single<AuthUser> = authRepository.login(login, remember)

    fun register(registration: Registration, remember: Boolean = true): Single<AuthUser> =
        authRepository.register(registration, remember)

    fun logout(): Completable = authRepository.logout()
}
