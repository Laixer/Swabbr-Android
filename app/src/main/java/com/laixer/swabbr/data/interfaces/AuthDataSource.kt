package com.laixer.swabbr.data.interfaces

import com.laixer.swabbr.domain.model.Login
import com.laixer.swabbr.domain.model.Registration
import com.laixer.swabbr.domain.model.TokenWrapper
import io.reactivex.Completable
import io.reactivex.Single

/**
 * Caching for authentication data.
 */
interface AuthCacheDataSource {

    val key: String get() = "AUTH"

    fun set(tokenWrapper: TokenWrapper): Single<TokenWrapper>

    fun get(): Single<TokenWrapper>

    fun logout(): Completable
}

/**
 *  Data source for authentication related operations.
 */
interface AuthDataSource {

    fun login(login: Login): Single<TokenWrapper>

    fun register(registration: Registration): Completable

    fun logout(): Completable
}
