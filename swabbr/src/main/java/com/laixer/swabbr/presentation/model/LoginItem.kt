package com.laixer.swabbr.presentation.model

import com.laixer.swabbr.domain.model.Login

data class LoginItem(
    val username: String,
    val password: String
)

fun LoginItem.mapToDomain(): Login =
    Login(
        this.username,
        this.password
    )
