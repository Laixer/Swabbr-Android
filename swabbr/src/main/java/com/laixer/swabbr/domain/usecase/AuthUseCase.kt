package com.laixer.swabbr.domain.usecase

import com.laixer.swabbr.domain.model.Login
import com.laixer.swabbr.domain.model.Registration
import com.laixer.swabbr.domain.model.Settings
import com.laixer.swabbr.domain.model.User
import com.laixer.swabbr.domain.repository.AuthRepository
import io.reactivex.Single

class AuthUseCase constructor(private val authRepository: AuthRepository) {

    fun login(login: Login): Single<Pair<Pair<String, User>, Settings>> = authRepository.login(login)

    fun register(registration: Registration): Single<Pair<Pair<String, User>, Settings>> =
        authRepository.register(registration)

    fun getToken(): Single<String> = authRepository.getToken()
}
