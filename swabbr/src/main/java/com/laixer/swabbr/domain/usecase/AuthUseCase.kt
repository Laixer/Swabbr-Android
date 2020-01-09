package com.laixer.swabbr.domain.usecase

import com.laixer.swabbr.domain.model.AuthUser
import com.laixer.swabbr.domain.model.Login
import com.laixer.swabbr.domain.model.Registration
import com.laixer.swabbr.domain.model.Settings
import com.laixer.swabbr.domain.model.User
import com.laixer.swabbr.domain.repository.AuthRepository
import com.laixer.swabbr.domain.repository.SettingsRepository
import com.laixer.swabbr.domain.repository.UserRepository
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.functions.Function3

class AuthUseCase constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val settingsRepository: SettingsRepository
) {

    fun login(login: Login): Single<AuthUser> =
        authRepository.login(login)
            .flatMap {
                Single.zip(
                    Single.just(it.accessToken),
                    userRepository.set(it.user),
                    settingsRepository.set(it.userSettings, false),
                    Function3<String, User, Settings, AuthUser>
                    { auth, user, settings ->
                        AuthUser(
                            auth,
                            user,
                            settings
                        )
                    }
                )
            }

    fun register(registration: Registration): Single<AuthUser> =
        authRepository.register(registration)
            .flatMap {
                Single.zip(
                    Single.just(it.accessToken),
                    userRepository.set(it.user),
                    settingsRepository.set(it.userSettings, false),
                    Function3<String, User, Settings, AuthUser>
                    { auth, user, settings ->
                        AuthUser(
                            auth,
                            user,
                            settings
                        )
                    }
                )
            }

    fun logout(): Completable = authRepository.logout()
}
