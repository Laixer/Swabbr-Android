package com.laixer.swabbr.domain.model

import java.time.ZonedDateTime
import java.util.UUID

data class Reaction(
    val id: UUID,
    val userId: UUID,
    val vlogId: UUID,
    val datePosted: ZonedDateTime
)
