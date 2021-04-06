package com.laixer.swabbr.presentation.model

import com.laixer.swabbr.domain.model.UserComplete
import com.laixer.swabbr.domain.model.UserUpdatableProperties
import com.laixer.swabbr.domain.types.FollowMode
import com.laixer.swabbr.domain.types.Gender
import java.io.File
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.*

/**
 *  Item containing all properties of a user that we can update. Each
 *  field is nullable, leaving it as null skips updating for that field.
 *
 *  Note that each item has its default set to null so we only have to
 *  assign the properties we wish to update.
 */
class UserUpdatablePropertiesItem(
    var firstName: String? = null,
    var lastName: String? = null,
    var gender: Gender? = null,
    var country: String? = null,
    var birthDate: LocalDate? = null,
    var timeZone: ZoneOffset? = null,
    var nickname: String? = null,
    var profileImageFile: File?,
    var latitude: Double? = null,
    var longitude: Double? = null,
    var isPrivate: Boolean? = null,
    var dailyVlogRequestLimit: Int? = null,
    var followMode: FollowMode? = null,
    var interest1 : String? = null,
    var interest2 : String? = null,
    var interest3 : String? = null
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
    profileImageFile,
    latitude,
    longitude,
    isPrivate,
    dailyVlogRequestLimit,
    followMode,
    interest1,
    interest2,
    interest3
)

/**
 *  Extracts a [UserUpdatablePropertiesItem] object from a [UserCompleteItem] object.
 */
fun UserCompleteItem.extractUpdatableProperties(): UserUpdatablePropertiesItem = UserUpdatablePropertiesItem(
    firstName,
    lastName,
    gender,
    country,
    birthDate,
    timeZone,
    nickname,
    null,
    latitude,
    longitude,
    isPrivate,
    dailyVlogRequestLimit,
    followMode,
    interest1,
    interest2,
    interest3
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
    null,
    latitude,
    longitude,
    isPrivate,
    dailyVlogRequestLimit,
    followMode,
    interest1,
    interest2,
    interest3
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
    profileImageFile,
    latitude,
    longitude,
    isPrivate,
    dailyVlogRequestLimit,
    followMode,
    interest1,
    interest2,
    interest3
)

