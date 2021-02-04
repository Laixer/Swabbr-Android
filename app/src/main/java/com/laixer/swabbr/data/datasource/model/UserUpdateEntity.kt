package com.laixer.swabbr.data.datasource.model

import com.laixer.swabbr.domain.model.UserComplete
import com.laixer.swabbr.domain.model.UserUpdatableProperties
import com.laixer.swabbr.domain.types.FollowMode
import com.laixer.swabbr.domain.types.Gender
import com.squareup.moshi.Json
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.ZonedDateTime

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
    @field:Json(name = "profileImageBase64Encoded") val profileImage: String?,
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
    profileImage,
    latitude,
    longitude,
    isPrivate,
    dailyVlogRequestLimit,
    followMode?.ordinal
)
