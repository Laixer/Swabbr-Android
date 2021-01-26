package com.laixer.swabbr.domain.model

import com.laixer.swabbr.domain.types.FollowMode
import com.laixer.swabbr.domain.types.Gender
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.ZonedDateTime

/**
 * Object used to register a new user. Note that almost all fields
 * are nullable and thus are not required.
 */
data class Registration(
    val email: String,
    val password: String,
    val nickname: String,
    val firstName: String?,
    val lastName: String?,
    val gender: Gender?,
    val country: String?,
    val birthDate: LocalDate?,
    val timezone: ZoneOffset?,
    val profileImage: String?,
    val latitude: Double?,
    val longitude: Double?,
    val isPrivate: Boolean?,
    val dailyVlogRequestLimit: Int?,
    val followMode: FollowMode?
)
