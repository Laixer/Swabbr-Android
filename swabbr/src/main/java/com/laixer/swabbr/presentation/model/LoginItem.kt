package com.laixer.swabbr.presentation.model

import com.laixer.swabbr.domain.model.Login

data class LoginItem(
    val username: String,
    val password: String,
    val remember: Boolean
)

fun LoginItem.mapToDomain(): Login =
    Login(
        this.username,
        this.password,
        this.remember
    )
