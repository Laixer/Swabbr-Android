package com.laixer.swabbr.data.model

import android.net.Uri
import com.laixer.swabbr.domain.model.UserComplete
import com.laixer.swabbr.domain.types.FollowMode
import com.laixer.swabbr.domain.types.Gender
import com.squareup.moshi.Json
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.*

/**
 * Entity containing all user data.
 */
data class UserCompleteEntity(
    @field:Json(name = "id") val id: UUID,
    @field:Json(name = "firstName") val firstName: String?,
    @field:Json(name = "lastName") val lastName: String?,
    @field:Json(name = "gender") val gender: Int,
    @field:Json(name = "country") val country: String?,
    @field:Json(name = "birthDate") val birthDate: LocalDate?,
    @field:Json(name = "timeZone") val timeZone: ZoneOffset?,
    @field:Json(name = "nickname") val nickname: String,
    @field:Json(name = "profileImageDateUpdated") val profileImageDateUpdated: ZonedDateTime?,
    @field:Json(name = "profileImageUri") val profileImageUri: Uri?,
    @field:Json(name = "profileImageUploadUri") val profileImageUploadUri: Uri?,
    @field:Json(name = "latitude") val latitude: Double?,
    @field:Json(name = "longitude") val longitude: Double?,
    @field:Json(name = "isPrivate") val isPrivate: Boolean,
    @field:Json(name = "dailyVlogRequestLimit") val dailyVlogRequestLimit: Int,
    @field:Json(name = "followMode") val followMode: Int
)

/**
 * Map a user complete from data to domain.
 */
fun UserCompleteEntity.mapToDomain(): UserComplete = UserComplete(
    id,
    firstName,
    lastName,
    Gender.values()[gender],
    country,
    birthDate,
    timeZone,
    nickname,
    profileImageDateUpdated,
    profileImageUri,
    profileImageUploadUri,
    latitude,
    longitude,
    isPrivate,
    dailyVlogRequestLimit,
    FollowMode.values()[followMode]
)
