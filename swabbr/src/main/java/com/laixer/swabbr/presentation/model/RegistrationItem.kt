package com.laixer.swabbr.presentation.model

import com.laixer.swabbr.domain.model.Gender
import com.laixer.swabbr.domain.model.PushNotificationPlatform
import com.laixer.swabbr.domain.model.Registration
import java.net.URL
import java.time.ZoneOffset
import java.time.ZonedDateTime

data class RegistrationItem(
//    val firstName: String,
//    val lastName: String,
//    val gender: Gender,
//    val country: String,
    val email: String,
    val password: String,
//    val birthdate: ZonedDateTime,
//    val timezone: ZoneOffset,
    val nickname: String,
    val profileImage: String?,
//    val phoneNumber: String,
    val platform: PushNotificationPlatform,
    val handle: String
)

fun RegistrationItem.mapToDomain(): Registration = Registration(
//    this.firstName,
//    this.lastName,
//    this.gender,
//    this.country,
    this.email,
    this.password,
//    this.birthdate,
//    this.timezone,
    this.nickname,
    this.profileImage,
//    this.phoneNumber,
    this.platform,
    this.handle
)
