package com.laixer.swabbr.domain.model

import com.auth0.android.jwt.JWT
import java.time.Duration
import java.time.ZonedDateTime
import java.util.*

/**
 * Wrapper containing a jwt token and some metadata.
 */
data class TokenWrapper(
    val userId: UUID,
    val token: JWT,
    val refreshToken: String,
    val tokenExpirationInMinutes: Int,
    val refreshTokenExpirationInMinutes: Int
)
