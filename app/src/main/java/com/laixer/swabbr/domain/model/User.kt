package com.laixer.swabbr.domain.model

import com.laixer.swabbr.domain.types.Gender
import java.util.*

/**
 * Object representing a single user. Note that this
 * user does not contain any personal properties. See
 * UserComplete for the complete package.
 */
data class User(
    val id: UUID,
    val firstName: String?,
    val lastName: String?,
    val gender: Gender,
    val country: String?,
    val nickname: String,
    val profileImage: String?
)
