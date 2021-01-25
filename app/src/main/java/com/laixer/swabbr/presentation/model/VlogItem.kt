package com.laixer.swabbr.presentation.model

import android.net.Uri
import com.laixer.swabbr.domain.model.Vlog
import com.laixer.swabbr.domain.model.VlogLikeSummary
import com.laixer.swabbr.domain.types.VlogStatus
import java.time.ZonedDateTime
import java.util.*

// TODO Vlog like summary correct?
/**
 * Object representing a single vlog. This also has a vlog like
 * summary attached to it which is nullable.
 * Note: [length] is in seconds.
 */
data class VlogItem(
    val id: UUID,
    val userId: UUID,
    val isPrivate: Boolean,
    val dateCreated: ZonedDateTime,
    val views: Int,
    val length: Int?,
    val vlogStatus: VlogStatus,
    val videoUri: Uri,
    val thumbnailUri: Uri,
    val vlogLikeSummary: VlogLikeSummaryItem?
)


/**
 *  Map a vlog from presentation to domain. Note that this does not
 *  take the vlog like summary with it, since this concatenation of
 *  entities does not exist in the domain layer.
 */
fun Vlog.mapToDomain(): Vlog = Vlog(
    id,
    userId,
    isPrivate,
    dateStarted,
    views,
    length,
    vlogStatus,
    videoUri,
    thumbnail
)

// TODO Is this correct? Seems dangerous.
/**
 *  Map a vlog from domain to presentation. Note that this does
 *  not map the vlog like summary, since none is available when
 *  calling this extension method. Assign this in some other way.
 */
fun Vlog.mapToPresentation(): VlogItem = VlogItem(
    id,
    userId,
    isPrivate,
    dateStarted,
    views,
    length,
    vlogStatus,
    videoUri,
    thumbnail,
    null
)

/**
 *  Map a vlog from domain to presentation.
 *
 *  @param The vlog like summary for the vlog.
 */
fun Vlog.mapToPresentation(vlogLikeSummary: VlogLikeSummary): VlogItem = VlogItem(
    id,
    userId,
    isPrivate,
    dateStarted,
    views,
    length,
    vlogStatus,
    videoUri,
    thumbnail,
    vlogLikeSummary.mapToPresentation()
)
