package com.laixer.swabbr.data.datasource.model

import com.laixer.swabbr.domain.model.UserWithStats
import com.laixer.swabbr.domain.types.Gender
import com.squareup.moshi.Json
import java.util.*

/**
 * Entity representing a user with its statistics.
 */
data class UserWithStatsEntity(
    @field:Json(name = "id") val id: UUID,
    @field:Json(name = "firstName") val firstName: String?,
    @field:Json(name = "lastName") val lastName: String?,
    @field:Json(name = "gender") val gender: Int,
    @field:Json(name = "country") val country: String?,
    @field:Json(name = "nickname") val nickname: String,
    @field:Json(name = "profileImageBase64Encoded") val profileImage: String?,
    @field:Json(name = "totalLikes") val totalLikes: Int,
    @field:Json(name = "totalFollowers") val totalFollowers: Int,
    @field:Json(name = "totalFollowing") val totalFollowing: Int,
    @field:Json(name = "totalReactionsGiven") val totalReactionsGiven: Int,
    @field:Json(name = "totalReactionsReceived") val totalReactionsReceived: Int,
    @field:Json(name = "totalVlogs") val totalVlogs: Int,
    @field:Json(name = "totalViews") val totalViews: Int
)

/**
 * Map a user with stats from data to domain.
 */
fun UserWithStatsEntity.mapToDomain(): UserWithStats = UserWithStats(
    id,
    firstName,
    lastName,
    Gender.values()[gender],
    country,
    nickname,
    profileImage,
    totalLikes,
    totalFollowers,
    totalFollowing,
    totalReactionsGiven,
    totalReactionsReceived,
    totalVlogs,
    totalViews
)