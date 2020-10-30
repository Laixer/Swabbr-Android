package com.laixer.swabbr.domain.model

import java.time.ZonedDateTime
import java.util.UUID

data class Reaction(
    val id: UUID,
    val userId: UUID,
    val targetVlogId: UUID,
    val createDate: ZonedDateTime,
    val isPrivate: Boolean
)

data class UploadReaction(
    val reaction: Reaction,
    val uploadUrl: String
)
