package com.laixer.swabbr.domain.model

import android.net.Uri
import com.laixer.swabbr.domain.types.VlogStatus
import com.squareup.moshi.Json
import java.time.ZonedDateTime
import java.util.UUID

/**
 * Object representing a like summary for a vlog.
 */
data class VlogLikeSummary(
    val vlogId: UUID,
    val totalLikes: Int,
    val users: List<User>
)
