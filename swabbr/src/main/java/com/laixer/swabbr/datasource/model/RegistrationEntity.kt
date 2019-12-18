package com.laixer.swabbr.datasource.model

import com.laixer.swabbr.domain.model.Registration
import com.squareup.moshi.Json

data class RegistrationEntity(
    @field:Json(name = "firstName") val firstName: String,
    @field:Json(name = "lastName") val lastName: String,
    @field:Json(name = "gender") val gender: Int,
    @field:Json(name = "country") val country: String,
    @field:Json(name = "email") val email: String,
    @field:Json(name = "password") val password: String,
    @field:Json(name = "birthdate") val birthdate: String,
    @field:Json(name = "timezone") val timezone: String,
    @field:Json(name = "nickname") val nickname: String,
    @field:Json(name = "profileImageUrl") val profileImageUrl: String,
    @field:Json(name = "isPrivate") val isPrivate: Boolean,
    @field:Json(name = "phoneNumber") val phoneNumber: String
)

fun Registration.mapToData(): RegistrationEntity = RegistrationEntity(
    firstName,
    lastName,
    gender,
    country,
    email,
    password,
    birthdate.toString(),
    timezone,
    nickname,
    profileImageUrl,
    isPrivate,
    phoneNumber
)
