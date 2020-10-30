package com.laixer.swabbr.data.datasource.model

import com.laixer.swabbr.domain.model.Gender
import com.laixer.swabbr.domain.model.SimplifiedUser
import com.laixer.swabbr.domain.model.User
import com.squareup.moshi.Json
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.TimeZone
import java.util.UUID

data class UserEntity(
    @field:Json(name = "id") val id: String,
    // User info
    @field:Json(name = "firstName") val firstName: String?,
    @field:Json(name = "lastName") val lastName: String?,
    @field:Json(name = "email") val email: String,
    @field:Json(name = "country") val country: String?,
    @field:Json(name = "gender") val gender: String,

    @field:Json(name = "timezone") val timezone: String,
    // Stats
    @field:Json(name = "totalVlogs") val totalVlogs: Int,
    @field:Json(name = "totalFollowers") val totalFollowers: Int,
    @field:Json(name = "totalFollowing") val totalFollowing: Int,
    // Profile
    @field:Json(name = "nickname") val nickname: String,
    @field:Json(name = "profileImageBase64Encoded") val profileImage: String?,
    @field:Json(name = "birthDate") val birthdate: String?,
    @field:Json(name = "isPrivate") val isPrivate: Boolean
)

data class SimplifiedUserEntity(
    @field:Json(name = "id") val id: String,
    @field:Json(name = "nickName") val nickname: String
)

data class FollowingResponse(
    @field:Json(name = "following") val following: List<UserEntity>
)

fun SimplifiedUserEntity.mapToDomain(): SimplifiedUser = SimplifiedUser(
    UUID.fromString(id),
    nickname
)

fun SimplifiedUser.mapToData(): SimplifiedUserEntity = SimplifiedUserEntity(
    id.toString(),
    nickname
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
    profileImage,
    birthdate?.let { LocalDate.parse(it.split("T")[0]) },
    isPrivate
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
    profileImage,
    birthdate?.atStartOfDay()?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
    isPrivate
)

// Have to use Collection instead of List because Java sucks and erases types which causes same type signatures for List<T> functions.
fun Collection<SimplifiedUserEntity>.mapToDomain(): List<SimplifiedUser> = map { it.mapToDomain() }


fun List<User>.mapToData(): List<UserEntity> = map { it.mapToData() }
fun List<UserEntity>.mapToDomain(): List<User> = map { it.mapToDomain() }
