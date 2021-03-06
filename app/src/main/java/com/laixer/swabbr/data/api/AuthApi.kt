package com.laixer.swabbr.data.api

import com.laixer.swabbr.data.model.*
import com.laixer.swabbr.domain.types.SortingOrder
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.*
import java.util.*

/**
 *  Interface for authentication related API calls. Note that
 *  the No-Authentication header is appended to notify our
 *  AuthInterceptor that we don't require a token. TODO This.
 */
interface AuthApi {
    @POST("authentication/login")
    @Headers("No-Authentication: true")
    fun login(@Body login: LoginEntity): Single<TokenWrapperEntity>

    @POST("authentication/register")
    @Headers("No-Authentication: true")
    fun register(@Body registration: RegistrationEntity): Completable

    @POST("authentication/logout")
    fun logout(): Completable

    @POST("authentication/delete")
    fun delete(): Completable
}
