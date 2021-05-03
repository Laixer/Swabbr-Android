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
    val videoUri: Uri?,
    val thumbnailUri: Uri?,
    val vlogLikeSummary: VlogLikeSummaryItem?
) {
    companion object ForPosting {
        /**
         *  Generates a new [VlogItem] based on the properties
         *  required for posting a vlog. All other properties
         *  are either left at null or are set to their defaults.
         *
         *  @param id Vlog id as specified by the backend.
         *  @param isPrivate Public access modifier.
         */
        fun createForPosting(id: UUID, isPrivate: Boolean): VlogItem = VlogItem(
            id = id,
            userId = UUID(0, 0), // TODO Suboptimal, represents an empty uuid.
            isPrivate = isPrivate,
            dateCreated = ZonedDateTime.now(),
            views = 0,
            length = 0, // TODO Pass length?
            vlogStatus = VlogStatus.UP_TO_DATE,
            videoUri = null,
            thumbnailUri = null,
            vlogLikeSummary = null
        )
    }
}

/**
 *  Map a vlog from presentation to domain. Note that this does not
 *  take the vlog like summary with it, since this concatenation of
 *  entities does not exist in the domain layer.
 */
fun VlogItem.mapToDomain(): Vlog = Vlog(
    id,
    userId,
    isPrivate,
    dateCreated,
    views,
    length,
    vlogStatus,
    videoUri,
    thumbnailUri
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
