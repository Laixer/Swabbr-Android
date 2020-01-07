package com.laixer.swabbr.datasource.model

import com.laixer.swabbr.domain.model.Login
import com.squareup.moshi.Json

data class LoginEntity(
    @field:Json(name = "email") val username: String,
    @field:Json(name = "password") val password: String,
    @field:Json(name = "rememberMe") val rememberMe: Boolean
)

fun Login.mapToData(): LoginEntity = LoginEntity(username, password, remember)
