package com.laixer.swabbr.domain.model

import android.net.Uri
import com.laixer.swabbr.domain.types.VlogStatus
import java.time.ZonedDateTime
import java.util.*

/**
 * Object representing a single vlog.
 * Note: [length] is in seconds.
 */
data class Vlog(
    val id: UUID,
    val userId: UUID,
    val isPrivate: Boolean,
    val dateStarted: ZonedDateTime,
    val views: Int,
    val length: Int?,
    val vlogStatus: VlogStatus,
    val videoUri: Uri,
    val thumbnail: Uri
)
