package com.laixer.swabbr.presentation.model

import android.net.Uri
import com.laixer.swabbr.domain.model.User
import com.laixer.swabbr.domain.types.FollowRequestStatus
import com.laixer.swabbr.domain.types.Gender
import java.time.ZonedDateTime
import java.util.*

// TODO Look at inheritance
open class UserItem(
    val id: UUID,
    val firstName: String?,
    val lastName: String?,
    val gender: Gender,
    val country: String?,
    val nickname: String,
    val profileImageDateUpdated: ZonedDateTime?,
    val profileImageUri: Uri?
) {
    /**
     *  Gets the user display name. When no first name and
     *  last name are present, this returns the nickname.
     */
    fun getDisplayName(): String {
        if (!firstName.isNullOrBlank() && !lastName.isNullOrBlank()) {
            return "$firstName $lastName"
        }

        if (!firstName.isNullOrBlank()) {
            return firstName
        }

        if (!lastName.isNullOrBlank()) {
            return lastName
        }

        return nickname
    }
}

/**
 *  Maps a single [UserItem] to a [UserWithRelationItem] based on a
 *  specified [requestingUserId] and [followRequestStatus].
 *
 *  @param requestingUserId The user on which the relation is based.
 *  @param followRequestStatus The follow request status of the relation.
 */
fun UserItem.mapToUserWithRelationItem(
    requestingUserId: UUID,
    followRequestStatus: FollowRequestStatus
): UserWithRelationItem = UserWithRelationItem(
    requestingUserId = requestingUserId,
    followRequestStatus = followRequestStatus,
    user = UserItem(
        id = id,
        firstName = firstName,
        lastName = lastName,
        gender = gender,
        country = country,
        nickname = nickname,
        profileImageDateUpdated = profileImageDateUpdated,
        profileImageUri = profileImageUri
    )
)

/**
 *  Maps a single [UserItem] to a [UserWithRelationItem] based on a
 *  specified [requestingUserId] and [followRequestStatus].
 *
 *  @param requestingUserId The user on which the relation is based for each item.
 *  @param followRequestStatus The follow request status of the relation for each item.
 */
fun List<UserItem>.mapToUserWithRelationItem(
    requestingUserId: UUID,
    followRequestStatus: FollowRequestStatus
): List<UserWithRelationItem> = map { it.mapToUserWithRelationItem(requestingUserId, followRequestStatus) }

/**
 * Map a user from presentation to domain.
 */
fun UserItem.mapToDomain(): User = User(
    id = id,
    firstName = firstName,
    lastName = lastName,
    gender = gender,
    country = country,
    nickname = nickname,
    profileImageDateUpdated = profileImageDateUpdated,
    profileImageUri = profileImageUri
)

/**
 * Map a user from domain to presentation.
 */
fun User.mapToPresentation(): UserItem = UserItem(
    id,
    firstName,
    lastName,
    gender,
    country,
    nickname,
    profileImageDateUpdated,
    profileImageUri
)

/**
 * Map a collection of users from domain to presentation.
 */
fun List<User>.mapToPresentation(): List<UserItem> = map { it.mapToPresentation() }

/**
 * Map a collection of users from presentation to domain
 */
fun List<UserItem>.mapToDomain(): List<User> = map { it.mapToDomain() }
