package com.laixer.swabbr.data.model

import com.auth0.android.jwt.JWT
import com.laixer.swabbr.domain.model.TokenWrapper
import com.squareup.moshi.Json
import java.time.Duration
import java.time.ZonedDateTime

// TODO Repair duration.
/**
 * Entity returned when the user logged in.
 */
data class TokenWrapperEntity(
    @field:Json(name = "token") val jwtToken: String,
    @field:Json(name = "dateCreated") val dateCreated: ZonedDateTime
    //@field:Json(name = "tokenExpirationTimeSpan") val tokenExpirationTimeSpan: Duration
)

/**
 * Map a token wrapper from data to domain.
 */
fun TokenWrapperEntity.mapToDomain(): TokenWrapper = TokenWrapper(
    JWT(jwtToken),
    dateCreated,
    Duration.ZERO
)
