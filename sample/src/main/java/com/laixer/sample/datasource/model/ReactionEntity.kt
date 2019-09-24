package com.laixer.sample.datasource.model

import com.laixer.sample.domain.model.Reaction
import com.squareup.moshi.Json
import java.util.*

data class ReactionEntity(
    @field:Json(name = "userId") val userId: String,
    @field:Json(name = "vlogId") val vlogId: String,
    @field:Json(name = "id") val id: String,
    @field:Json(name = "duration") val duration: String,
    @field:Json(name = "postDate") val postDate: String
)

fun ReactionEntity.mapToDomain(): Reaction = Reaction(userId, vlogId, id, duration, postDate)

fun List<ReactionEntity>.mapToDomain(): List<Reaction> = map { it.mapToDomain() }
