package com.laixer.swabbr.datasource.model

import com.squareup.moshi.Json

data class FollowStatusEntity(
    @field:Json(name = "status") val status: String
)

fun FollowStatusEntity.mapToDomain(): String = status