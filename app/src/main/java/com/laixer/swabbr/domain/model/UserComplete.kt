package com.laixer.swabbr.domain.model

import android.net.Uri
import com.laixer.swabbr.domain.types.FollowMode
import com.laixer.swabbr.domain.types.Gender
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.*

/**
 * Object representing a single user including all
 * its data available.
 */
data class UserComplete(
    val id: UUID,
    val firstName: String?,
    val lastName: String?,
    val gender: Gender,
    val country: String?,
    val birthDate: LocalDate?,
    val timeZone: ZoneOffset?,
    val nickname: String,
    val hasProfileImage: Boolean,
    val profileImageUri: Uri?,
    val profileImageUploadUri: Uri?,
    val latitude: Double?,
    val longitude: Double?,
    val isPrivate: Boolean,
    val dailyVlogRequestLimit: Int,
    val followMode: FollowMode,
    val interest1 : String? = null,
    val interest2 : String? = null,
    val interest3 : String? = null
)
