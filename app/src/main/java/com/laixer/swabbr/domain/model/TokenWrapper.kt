package com.laixer.swabbr.domain.model

import com.auth0.android.jwt.JWT
import java.time.Duration
import java.time.ZonedDateTime

/**
 * Wrapper containing a jwt token and some metadata.
 */
data class TokenWrapper(
    val jwtToken: JWT,
    var dateCreated: ZonedDateTime,
    var tokenExpirationTimeSpan: Duration
)
