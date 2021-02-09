package com.laixer.swabbr.domain.usecase

import com.laixer.swabbr.domain.model.Login
import com.laixer.swabbr.domain.model.Registration
import com.laixer.swabbr.domain.model.TokenWrapper
import com.laixer.swabbr.domain.interfaces.AuthRepository
import com.laixer.swabbr.domain.types.PushNotificationPlatform
import io.reactivex.Completable
import io.reactivex.Single

/**
 *  Use case for registration and for user login / logout.
 */
class AuthUseCase constructor(
    private val authRepository: AuthRepository
) {
    /**
     *  Log the current user in.
     *
     *  @param name User login name (being email).
     *  @param password User password.
     *  @param fbToken Firebase token / device handle.
     */
    fun login(name: String, password: String, fbToken: String): Single<TokenWrapper> =
        authRepository.login(Login(name, password, true, PushNotificationPlatform.FCM, fbToken))

    /**
     *  Register a new user.
     *
     *  @param registration Registration object.
     */
    fun register(registration: Registration): Completable =
        authRepository.register(registration)

    /**
     *  Log the currently authenticated user out.
     */
    fun logout(): Completable = authRepository.logout()
}
