package com.laixer.swabbr.datasource.model

import com.laixer.swabbr.domain.model.FollowStatus
import com.squareup.moshi.Json

data class FollowStatusEntity(
    @field:Json(name = "status") val followStatus: String
)

fun FollowStatusEntity.mapToDomain(): FollowStatus = FollowStatus.values().first { it.value == followStatus }
fun FollowStatus.mapToData(): FollowStatusEntity = FollowStatusEntity(this.value)
