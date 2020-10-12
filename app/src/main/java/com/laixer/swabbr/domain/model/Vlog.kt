package com.laixer.swabbr.domain.model

import android.net.Uri
import java.net.URL
import java.time.ZonedDateTime
import java.util.UUID

data class Vlog(
    val data: VlogData,
    val vlogLikeSummary: VlogLikeSummary,
    val thumbnailUri: Uri
)

data class VlogData(
    val id: UUID,
    val userId: UUID,
    val isPrivate: Boolean,
    val dateStarted: ZonedDateTime,
    val views: Int
)

data class VlogLikeSummary(
    val vlogId: UUID,
    val totalLikes: Int,
    val simplifiedUsers: List<SimplifiedUser>
)
