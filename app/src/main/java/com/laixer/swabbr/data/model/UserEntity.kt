package com.laixer.swabbr.data.model

import android.net.Uri
import com.laixer.swabbr.domain.model.User
import com.laixer.swabbr.domain.types.Gender
import com.squareup.moshi.Json
import java.time.ZonedDateTime
import java.util.*

/**
 * Entity representing a single user. No personal information
 * is contained within this entity, see UserComplete.
 */
data class UserEntity(
    @field:Json(name = "id") val id: UUID,
    @field:Json(name = "firstName") val firstName: String?,
    @field:Json(name = "lastName") val lastName: String?,
    @field:Json(name = "gender") val gender: Int,
    @field:Json(name = "country") val country: String?,
    @field:Json(name = "nickname") val nickname: String,
    @field:Json(name = "profileImageDateUpdated") val profileImageDateUpdated: ZonedDateTime?,
    @field:Json(name = "profileImageUri") val profileImageUri: Uri?
)

/**
 * Map a user from data to domain.
 */
fun UserEntity.mapToDomain(): User = User(
    id,
    firstName,
    lastName,
    Gender.values()[gender],
    country,
    nickname,
    profileImageDateUpdated,
    profileImageUri
)

/**
 * Map a user from domain to data.
 */
fun User.mapToData(): UserEntity = UserEntity(
    id,
    firstName,
    lastName,
    gender.ordinal,
    country,
    nickname,
    profileImageDateUpdated,
    profileImageUri
)

/**
 * Map a collection of users from domain to data.
 */
fun List<User>.mapToData(): List<UserEntity> = map { it.mapToData() }

/**
 * Map a collection of users from data to domain
 */
fun List<UserEntity>.mapToDomain(): List<User> = map { it.mapToDomain() }
