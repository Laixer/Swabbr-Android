package com.laixer.swabbr.data.model

import com.laixer.swabbr.domain.model.Registration
import com.squareup.moshi.Json
import java.time.LocalDate
import java.time.ZoneOffset

/**
 * Entity used to register a new user. Note that almost all fields
 * are nullable and thus are not required.
 */
data class RegistrationEntity(
    @field:Json(name = "email") val email: String,
    @field:Json(name = "password") val password: String,
    @field:Json(name = "nickname") val nickname: String,
    @field:Json(name = "firstName") val firstName: String?,
    @field:Json(name = "lastName") val lastName: String?,
    @field:Json(name = "gender") val gender: Int?,
    @field:Json(name = "country") val country: String?,
    @field:Json(name = "birthDate") val birthDate: LocalDate?,
    @field:Json(name = "timezone") val timezone: ZoneOffset?,
    @field:Json(name = "profileImageBase64Encoded") val profileImage: String?,
    @field:Json(name = "latitude") val latitude: Double?,
    @field:Json(name = "longitude") val longitude: Double?,
    @field:Json(name = "isPrivate") val isPrivate: Boolean?,
    @field:Json(name = "dailyVlogRequestLimit") val dailyVlogRequestLimit: Int?,
    @field:Json(name = "followMode") val followMode: Int?
)

/**
 * Map a registration object from domain to data.
 */
fun Registration.mapToData(): RegistrationEntity = RegistrationEntity(
    email,
    password,
    nickname,
    firstName,
    lastName,
    gender?.ordinal,
    country,
    birthDate,
    timezone,
    profileImage,
    latitude,
    longitude,
    isPrivate,
    dailyVlogRequestLimit,
    followMode?.ordinal
)
