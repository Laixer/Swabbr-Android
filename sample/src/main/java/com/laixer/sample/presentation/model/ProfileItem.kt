package com.laixer.sample.presentation.model

import com.laixer.sample.domain.model.User

data class ProfileItem(
    val id: String,
    val nickname: String,
    val firstName: String,
    val lastName: String
)

fun User.mapToPresentation(): ProfileItem =
    ProfileItem(
        this.id,
        this.nickname,
        this.firstName,
        this.lastName
    )
