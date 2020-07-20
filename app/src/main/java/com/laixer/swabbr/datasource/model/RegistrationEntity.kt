package com.laixer.swabbr.datasource.model

import com.laixer.swabbr.domain.model.Gender
import com.laixer.swabbr.domain.model.PushNotificationPlatform
import com.laixer.swabbr.domain.model.Registration
import com.squareup.moshi.Json
import java.net.URL
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

data class RegistrationEntity(
//    @field:Json(name = "firstName") val firstName: String,
//    @field:Json(name = "register_lastName") val register_lastName: String,
//    @field:Json(name = "gender") val gender: String,
//    @field:Json(name = "country") val country: String,
    @field:Json(name = "email") val email: String,
    @field:Json(name = "password") val password: String,
//    @field:Json(name = "birthdate") val birthdate: String,
    @field:Json(name = "timezone") val timezone: String,
    @field:Json(name = "nickname") val nickname: String,
    @field:Json(name = "profileImageBase64Encoded") val profileImage: String?,
//    @field:Json(name = "phoneNumber") val phoneNumber: String,
    @field:Json(name = "pushNotificationPlatform") val pushNotificationPlatform: String,
    @field:Json(name = "handle") val handle: String
)

fun Registration.mapToData(): RegistrationEntity = RegistrationEntity(
//    firstName,
//    register_lastName,
//    gender.value,
//    country,
    email,
    password,
//    birthdate.toInstant().toString(),
    "UTC${DateTimeFormatter.ofPattern("xxx").format(timezone)}",
    nickname,
    profileImage,
//    phoneNumber,
    pushNotificationPlatform.value,
    handle
)

fun RegistrationEntity.mapToDomain(): Registration = Registration(
//    firstName,
//    register_lastName,
//    Gender.values().first { it.value == gender },
//    country,
    email,
    password,
//    ZonedDateTime.parse(birthdate),
    ZoneOffset.of(timezone.replace("UTC", "")),
    nickname,
    profileImage,
//    phoneNumber,
    PushNotificationPlatform.values().first { it.value == pushNotificationPlatform },
    handle
)

fun List<Registration>.mapToData(): List<RegistrationEntity> = map { it.mapToData() }
fun List<RegistrationEntity>.mapToDomain(): List<Registration> = map { it.mapToDomain() }
