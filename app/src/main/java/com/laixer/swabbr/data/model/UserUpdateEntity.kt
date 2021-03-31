package com.laixer.swabbr.data.model

import com.laixer.swabbr.domain.model.UserUpdatableProperties
import com.squareup.moshi.Json
import java.time.LocalDate
import java.time.ZoneOffset

/**
 * Entity used to update a user. Any value left at null
 * will not be changed. Each field is nullable. Note that
 * any server-side nullable field will be set to null in
 * this case.
 */
data class UserUpdateEntity(
    @field:Json(name = "firstName") val firstName: String?,
    @field:Json(name = "lastName") val lastName: String?,
    @field:Json(name = "gender") val gender: Int?,
    @field:Json(name = "country") val country: String?,
    @field:Json(name = "birthDate") val birthDate: LocalDate?,
    @field:Json(name = "timeZone") val timeZone: ZoneOffset?,
    @field:Json(name = "nickname") val nickname: String?,
    @field:Json(name = "hasProfileImage") val hasProfileImage: Boolean?,
    @field:Json(name = "latitude") val latitude: Double?,
    @field:Json(name = "longitude") val longitude: Double?,
    @field:Json(name = "isPrivate") val isPrivate: Boolean?,
    @field:Json(name = "dailyVlogRequestLimit") val dailyVlogRequestLimit: Int?,
    @field:Json(name = "followMode") val followMode: Int?
)

/**
 *  Map a user update domain object to a user update data object.
 */
fun UserUpdatableProperties.mapToData(): UserUpdateEntity = UserUpdateEntity(
    firstName,
    lastName,
    gender?.ordinal,
    country,
    birthDate,
    timeZone,
    nickname,
    if (profileImageFile != null) true else null, // If we have a file, we have updated the profile image.
    latitude,
    longitude,
    isPrivate,
    dailyVlogRequestLimit,
    followMode?.ordinal
)
