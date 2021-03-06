package com.laixer.swabbr.domain.model

import com.laixer.swabbr.domain.types.FollowMode
import com.laixer.swabbr.domain.types.Gender
import java.io.File
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.*

/**
 *  Object containing all properties of a user that we can update. Each
 *  field is nullable, leaving it as [null] skips updating for that field.
 */
class UserUpdatableProperties(
    val firstName: String?,
    val lastName: String?,
    val gender: Gender?,
    val country: String?,
    val birthDate: LocalDate?,
    val timeZone: ZoneOffset?,
    val nickname: String?,
    val profileImageFile: File?,
    val latitude: Double?,
    val longitude: Double?,
    val isPrivate: Boolean?,
    val dailyVlogRequestLimit: Int?,
    val followMode: FollowMode?,
    val interest1 : String? = null,
    val interest2 : String? = null,
    val interest3 : String? = null
)
