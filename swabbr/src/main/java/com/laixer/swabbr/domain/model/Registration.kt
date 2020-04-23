package com.laixer.swabbr.domain.model

import java.net.URL
import java.time.ZoneOffset
import java.time.ZonedDateTime

data class Registration(
    val firstName: String,
    val lastName: String,
    val gender: Gender,
    val country: String,
    val email: String,
    val password: String,
    val birthdate: ZonedDateTime,
    val timezone: ZoneOffset,
    val nickname: String,
    val profileImageUrl: URL,
    val phoneNumber: String,
    val pushNotificationPlatform: PushNotificationPlatform,
    val handle: String
)
