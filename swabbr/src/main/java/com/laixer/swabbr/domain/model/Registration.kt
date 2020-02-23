package com.laixer.swabbr.domain.model

import java.util.Date

data class Registration(
    val firstName: String,
    val lastName: String,
    val gender: Int,
    val country: String,
    val email: String,
    val password: String,
    val birthdate: Date,
    val timezone: String,
    val nickname: String,
    val profileImageUrl: String,
    val isPrivate: Boolean,
    val phoneNumber: String
)
