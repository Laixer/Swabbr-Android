package com.laixer.swabbr.data.model

import android.net.Uri
import com.laixer.swabbr.domain.model.UserWithStats
import com.laixer.swabbr.domain.types.Gender
import com.squareup.moshi.Json
import java.time.ZonedDateTime
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
    @field:Json(name = "profileImageDateUpdated") val profileImageDateUpdated: ZonedDateTime?,
    @field:Json(name = "profileImageUri") val profileImageUri: Uri?,
    @field:Json(name = "totalLikesReceived") val totalLikesReceived: Int,
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
    id = id,
    firstName = firstName,
    lastName = lastName,
    gender = Gender.values()[gender],
    country = country,
    nickname = nickname,
    profileImageDateUpdated = profileImageDateUpdated,
    profileImageUri = profileImageUri,
    totalLikesReceived = totalLikesReceived,
    totalFollowers = totalFollowers,
    totalFollowing = totalFollowing,
    totalReactionsGiven = totalReactionsGiven,
    totalReactionsReceived = totalReactionsReceived,
    totalVlogs = totalVlogs,
    totalViews = totalViews
)
