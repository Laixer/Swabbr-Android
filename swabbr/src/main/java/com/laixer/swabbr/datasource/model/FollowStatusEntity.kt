package com.laixer.swabbr.datasource.model

import com.squareup.moshi.Json

data class FollowStatusEntity(
    @field:Json(name = "status") val status: Int
)

fun FollowStatusEntity.mapToDomain(): Int = status
