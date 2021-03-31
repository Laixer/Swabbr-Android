package com.laixer.swabbr.presentation.model

import android.net.Uri
import com.laixer.swabbr.domain.model.UserWithStats
import com.laixer.swabbr.domain.types.Gender
import java.time.ZonedDateTime
import java.util.*

/**
 *  Item representing a user with statistics.
 */
class UserWithStatsItem(
    id: UUID,
    firstName: String?,
    lastName: String?,
    gender: Gender,
    country: String?,
    nickname: String,
    profileImageDateUpdated: ZonedDateTime?,
    profileImageUri: Uri?,
    val totalLikesReceived: Int,
    var totalFollowers: Int, // TODO Modified for auth user view model, suboptimal. Can't use copy anymore due to non-data classes and polymorphism...
    val totalFollowing: Int,
    val totalReactionsGiven: Int,
    val totalReactionsReceived: Int,
    val totalVlogs: Int,
    val totalViews: Int
) : UserItem (id, firstName, lastName, gender, country, nickname, profileImageDateUpdated, profileImageUri)

/**
 * Map a user with stats from domain to presentation.
 */
fun UserWithStats.mapToPresentation(): UserWithStatsItem = UserWithStatsItem(
    id = id,
    firstName = firstName,
    lastName = lastName,
    gender = gender,
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
