package com.laixer.swabbr.presentation.model

import android.net.Uri
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
    val hasProfileImage: Boolean,
    val profileImageUri: Uri?,
    val profileImageUploadUri: Uri?,
    val latitude: Double?,
    val longitude: Double?,
    var isPrivate: Boolean,
    val dailyVlogRequestLimit: Int,
    val followMode: FollowMode,
    val interest1 : String? = null,
    val interest2 : String? = null,
    val interest3 : String? = null
)

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
    hasProfileImage,
    profileImageUri,
    profileImageUploadUri,
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
    hasProfileImage,
    profileImageUri,
    profileImageUploadUri,
    latitude,
    longitude,
    isPrivate,
    dailyVlogRequestLimit,
    followMode,
    interest1,
    interest2,
    interest3
)

fun UserCompleteItem.extractUser(): UserItem = UserItem(
    id,
    firstName,
    lastName,
    gender,
    country,
    nickname,
    hasProfileImage,
    profileImageUri
)
