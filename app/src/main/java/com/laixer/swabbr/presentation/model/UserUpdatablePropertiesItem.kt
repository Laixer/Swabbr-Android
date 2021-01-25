package com.laixer.swabbr.presentation.model

import com.laixer.swabbr.domain.model.UserUpdatableProperties
import com.laixer.swabbr.domain.types.FollowMode
import com.laixer.swabbr.domain.types.Gender
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.*

/**
 *  Item containing all properties of a user that we can update. Each
 *  field is nullable, leaving it as [null] skips updating for that field.
 */
class UserUpdatablePropertiesItem(
    var firstName: String?,
    var lastName: String?,
    var gender: Gender?,
    var country: String?,
    var birthDate: ZonedDateTime?,
    var timeZone: ZoneOffset?,
    var nickname: String?,
    var profileImage: String?,
    var latitude: Double?,
    var longitude: Double?,
    var isPrivate: Boolean?,
    var dailyVlogRequestLimit: Int?,
    var followMode: FollowMode?
)

/**
 *  Copies this into a new object with identical values.
 */
fun UserUpdatablePropertiesItem.copy(): UserUpdatablePropertiesItem = UserUpdatablePropertiesItem(
    firstName,
    lastName,
    gender,
    country,
    birthDate,
    timeZone,
    nickname,
    profileImage,
    latitude,
    longitude,
    isPrivate,
    dailyVlogRequestLimit,
    followMode
)

/**
 *  Map a user update object from domain to presentation.
 */
fun UserUpdatableProperties.mapToPresentation(): UserUpdatablePropertiesItem = UserUpdatablePropertiesItem(
    firstName,
    lastName,
    gender,
    country,
    birthDate,
    timeZone,
    nickname,
    profileImage,
    latitude,
    longitude,
    isPrivate,
    dailyVlogRequestLimit,
    followMode
)

/**
 *  Map a user update object from presentation to domain.
 */
fun UserUpdatablePropertiesItem.mapToDomain(): UserUpdatableProperties = UserUpdatableProperties(
    firstName,
    lastName,
    gender,
    country,
    birthDate,
    timeZone,
    nickname,
    profileImage,
    latitude,
    longitude,
    isPrivate,
    dailyVlogRequestLimit,
    followMode
)

