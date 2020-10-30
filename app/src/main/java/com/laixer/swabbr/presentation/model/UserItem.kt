package com.laixer.swabbr.presentation.model

import android.os.Parcel
import android.os.Parcelable
import com.laixer.swabbr.domain.model.Gender
import com.laixer.swabbr.domain.model.User
import java.time.LocalDate
import java.util.TimeZone
import java.util.UUID

data class UserItem(
    val id: UUID,
    var firstName: String?,
    var lastName: String?,
    var gender: Gender,
    var country: String?,
    val email: String,
    val timezone: TimeZone,
    val totalVlogs: Int,
    val totalFollowers: Int,
    val totalFollowing: Int,
    var nickname: String,
    var profileImage: String?,
    var birthdate: LocalDate?,
    var isPrivate: Boolean
) {

    fun equals(otheritem: UserItem): Boolean =
        firstName == otheritem.firstName
            && lastName == otheritem.lastName
            && gender == otheritem.gender
            && country == otheritem.country
            && birthdate == otheritem.birthdate
            && nickname == otheritem.nickname
            && profileImage == otheritem.profileImage
            && isPrivate == otheritem.isPrivate

}

fun User.mapToPresentation(): UserItem = UserItem(
    this.id,
    this.firstName,
    this.lastName,
    this.gender,
    this.country,
    this.email,
    this.timezone,
    this.totalVlogs,
    this.totalFollowers,
    this.totalFollowing,
    this.nickname,
    this.profileImage,
    this.birthdate,
    this.isPrivate
)

fun UserItem.mapToDomain(): User = User(
    this.id,
    this.firstName,
    this.lastName,
    this.gender,
    this.country,
    this.email,
    this.timezone,
    this.totalVlogs,
    this.totalFollowers,
    this.totalFollowing,
    this.nickname,
    this.profileImage,
    this.birthdate,
    this.isPrivate
)

fun List<UserItem>.mapToDomain(): List<User> = map { it.mapToDomain() }
fun List<User>.mapToPresentation(): List<UserItem> = map { it.mapToPresentation() }


