package com.laixer.swabbr.datasource.model

import com.laixer.swabbr.domain.model.Gender
import com.laixer.swabbr.domain.model.User
import com.squareup.moshi.Json
import java.net.URL
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.TimeZone
import java.util.UUID

data class UserEntity(
    @field:Json(name = "id") val id: String,
    // User info
    @field:Json(name = "firstName") val firstName: String,
    @field:Json(name = "lastName") val lastName: String,
    @field:Json(name = "email") val email: String,
    @field:Json(name = "country") val country: String,
    @field:Json(name = "gender") val gender: String,

    @field:Json(name = "timezone") val timezone: String,
    // Stats
    @field:Json(name = "totalVlogs") val totalVlogs: Int,
    @field:Json(name = "totalFollowers") val totalFollowers: Int,
    @field:Json(name = "totalFollowing") val totalFollowing: Int,
    // Profile
    @field:Json(name = "nickname") val nickname: String,
    @field:Json(name = "profileImageUrl") val profileImageUrl: String?,
    @field:Json(name = "birthDate") val birthdate: String
)

fun UserEntity.mapToDomain(): User = User(
    UUID.fromString(id),
    firstName,
    lastName,
    Gender.values().first { it.value == gender },
    country,
    email,
    TimeZone.getTimeZone(timezone),
    totalVlogs,
    totalFollowers,
    totalFollowing,
    nickname,
    URL(profileImageUrl ?: "https://api.adorable.io:443/avatars/285/$id"),
    LocalDate.parse(birthdate.split("T")[0])
)

fun User.mapToData(): UserEntity = UserEntity(
    id.toString(),
    firstName,
    lastName,
    email,
    country,
    gender.value,
    timezone.id,
    totalVlogs,
    totalFollowers,
    totalFollowing,
    nickname,
    profileImageUrl.toString(),
    birthdate.atStartOfDay().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
)

fun List<User>.mapToData(): List<UserEntity> = map { it.mapToData() }
fun List<UserEntity>.mapToDomain(): List<User> = map { it.mapToDomain() }
