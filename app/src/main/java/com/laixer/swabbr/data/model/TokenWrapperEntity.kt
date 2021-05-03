package com.laixer.swabbr.data.model

import com.auth0.android.jwt.JWT
import com.laixer.swabbr.domain.model.TokenWrapper
import com.squareup.moshi.Json
import java.time.ZonedDateTime
import java.util.*

/**
 *  Entity returned when the user logged in. Note that this does
 *  not read the value of dateCreated in the json response body,
 *  because this value is encapsulated in the token itself.
 */
data class TokenWrapperEntity(
    @field:Json(name = "userId") val userId: UUID,
    @field:Json(name = "token") val token: String,
    @field:Json(name = "refreshToken") val refreshToken: String,
    @field:Json(name = "tokenExpirationInMinutes") val tokenExpirationInMinutes: Int,
    @field:Json(name = "refreshTokenExpirationInMinutes") val refreshTokenExpirationInMinutes: Int
)

/**
 * Map a token wrapper from data to domain.
 */
fun TokenWrapperEntity.mapToDomain(): TokenWrapper = TokenWrapper(
    userId = userId,
    token = JWT(token),
    refreshToken = refreshToken,
    tokenExpirationInMinutes = tokenExpirationInMinutes,
    refreshTokenExpirationInMinutes = refreshTokenExpirationInMinutes
)
