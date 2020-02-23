package com.laixer.swabbr.datasource.model

import com.laixer.swabbr.domain.model.User
import com.squareup.moshi.Json

data class UserEntity(
    @field:Json(name = "id") val id: String,
    // User info
    @field:Json(name = "firstName") val firstName: String,
    @field:Json(name = "lastName") val lastName: String,
    @field:Json(name = "gender") val gender: String,
    @field:Json(name = "country") val country: String,
    @field:Json(name = "email") val email: String,
    @field:Json(name = "timezone") val timezone: String,
    // Stats
    @field:Json(name = "totalVlogs") val totalVlogs: Int,
    @field:Json(name = "totalFollowers") val totalFollowers: Int,
    @field:Json(name = "totalFollowing") val totalFollowing: Int,
    // Profile
    @field:Json(name = "nickname") val nickname: String,
    @field:Json(name = "profileImageUrl") val profileImageUrl: String,
    @field:Json(name = "birthdate") val birthdate: String,
    // Location
    @field:Json(name = "longitude") val longitude: Double,
    @field:Json(name = "latitude") val latitude: Double
)

fun UserEntity.mapToDomain(): User = User(
    id,
    firstName,
    lastName,
    gender,
    country,
    email,
    timezone,
    totalVlogs,
    totalFollowers,
    totalFollowing,
    nickname,
    profileImageUrl,
    birthdate,
    longitude,
    latitude
)

fun List<UserEntity>.mapToDomain(): List<User> = map { it.mapToDomain() }
