package com.laixer.swabbr.presentation.model

import com.laixer.swabbr.domain.model.Registration
import com.laixer.swabbr.domain.types.FollowMode
import com.laixer.swabbr.domain.types.Gender
import java.time.ZoneOffset
import java.time.ZonedDateTime

/**
 *  Item representing a registration. Note that a lot of these
 *  values are nullable and thus not required.
 */
data class RegistrationItem(
    val email: String,
    val password: String,
    val nickname: String,
    val firstName: String?,
    val lastName: String?,
    val gender: Gender?,
    val country: String?,
    val birthDate: ZonedDateTime?,
    val timeZone: ZoneOffset?,
    val profileImage: String?,
    val latitude: Double?,
    val longitude: Double?,
    val isPrivate: Boolean?,
    val dailyVlogRequestLimit: Int?,
    val followMode: FollowMode?
)

/**
 *  Map a registration from presentation to domain.
 */
fun RegistrationItem.mapToDomain(): Registration = Registration(
    email,
    password,
    nickname,
    firstName,
    lastName,
    gender,
    country,
    birthDate,
    timeZone,
    profileImage,
    latitude,
    longitude,
    isPrivate,
    dailyVlogRequestLimit,
    followMode
)
