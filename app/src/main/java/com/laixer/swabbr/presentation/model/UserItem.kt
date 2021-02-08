package com.laixer.swabbr.presentation.model

import com.laixer.swabbr.domain.model.User
import com.laixer.swabbr.domain.types.Gender
import java.util.*

data class UserItem(
    val id: UUID,
    val firstName: String?,
    val lastName: String?,
    val gender: Gender,
    val country: String?,
    val nickname: String,
    val profileImage: String?
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

    // TODO Do we need more?
    fun equals(compare: UserItem): Boolean = this.id == compare.id
//        firstName == compare.firstName
//            && lastName == compare.lastName
//            && gender == compare.gender
//            && country == compare.country
//            && birthdate == compare.birthdate
//            && nickname == compare.nickname
//            && profileImage == compare.profileImage
//            && isPrivate == compare.isPrivate
}

/**
 * Map a user from presentation to domain.
 */
fun UserItem.mapToDomain(): User = User(
    id,
    firstName,
    lastName,
    gender,
    country,
    nickname,
    profileImage
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
    profileImage
)

/**
 * Map a collection of users from domain to presentation.
 */
fun List<User>.mapToPresentation(): List<UserItem> = map { it.mapToPresentation() }

/**
 * Map a collection of users from presentation to domain
 */
fun List<UserItem>.mapToDomain(): List<User> = map { it.mapToDomain() }
