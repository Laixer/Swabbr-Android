package com.laixer.swabbr.presentation.model

import com.laixer.swabbr.domain.model.UserComplete
import com.laixer.swabbr.domain.types.FollowMode
import com.laixer.swabbr.domain.types.Gender
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.*

/**
 * TODO Change back var to val, see [AuthProfileDetailsFragment].
 *  Represents a user with personal details.
 */
data class UserCompleteItem(
    val id: UUID,
    var firstName: String?,
    var lastName: String?,
    val gender: Gender,
    val country: String?,
    val birthDate: LocalDate?,
    val timeZone: ZoneOffset?,
    var nickname: String,
    val profileImage: String?,
    val latitude: Double?,
    val longitude: Double?,
    var isPrivate: Boolean,
    val dailyVlogRequestLimit: Int,
    val followMode: FollowMode
) {
    /** TODO Duplicate functionality with [UserItem]. */
    /**
     *  Gets the user display name. When no first name and
     *  last name are present, this returns the nickname.
     */
    fun getDisplayName(): String {
        if (!firstName.isNullOrBlank() && !lastName.isNullOrBlank()) {
            return "$firstName $lastName"
        }

        if (!firstName.isNullOrBlank()) {
            return firstName as String
        }

        if (!lastName.isNullOrBlank()) {
            return lastName as String
        }

        return nickname
    }
}

/**
 *  Map a user complete object from domain to presentation.
 */
fun UserComplete.mapToPresentation(): UserCompleteItem = UserCompleteItem(
    id,
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
 *  Map a user complete object from presentation to domain.
 */
fun UserCompleteItem.mapToDomain(): UserComplete = UserComplete(
    id,
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
