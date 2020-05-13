package com.laixer.swabbr.domain.model

import java.time.ZonedDateTime
import java.util.UUID

data class Like(val id: UUID, val vlogId: UUID, val userId: UUID, val timeCreated: ZonedDateTime)
