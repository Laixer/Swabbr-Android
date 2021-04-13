package com.laixer.swabbr.domain.interfaces

import com.laixer.swabbr.domain.model.Login
import com.laixer.swabbr.domain.model.Registration
import com.laixer.swabbr.domain.model.TokenWrapper
import io.reactivex.Completable
import io.reactivex.Single

/**
 *  Interface for a user authentication repository.
 */
interface AuthRepository {

    fun login(login: Login): Single<TokenWrapper>

    fun register(registration: Registration): Completable

    fun logout(): Completable

    fun delete(): Completable
}
