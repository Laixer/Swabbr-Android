package com.laixer.swabbr.presentation.model

import com.laixer.swabbr.domain.model.Registration
import java.util.*

data class RegistrationItem(
    val firstName: String,
    val lastName: String,
    val gender: Int,
    val country: String,
    val email: String,
    val password: String,
    val birthdate: String,
    val timezone: String,
    val nickname: String,
    val profileImageUrl: String,
    val isPrivate: Boolean,
    val phoneNumber: String
)

fun RegistrationItem.mapToDomain(): Registration =
    Registration(
        this.firstName,
        this.lastName,
        this.gender,
        this.country,
        this.email,
        this.password,
        this.birthdate,
        this.timezone,
        this.nickname,
        this.profileImageUrl,
        this.isPrivate,
        this.phoneNumber
    )
