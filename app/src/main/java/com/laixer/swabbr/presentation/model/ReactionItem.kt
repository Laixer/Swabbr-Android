package com.laixer.swabbr.presentation.model

import android.net.Uri
import com.laixer.swabbr.domain.model.Reaction
import com.laixer.swabbr.domain.types.ReactionStatus
import java.time.ZonedDateTime
import java.util.*

// TODO Reaction thumbnails aren't used atm.
/**
 *  Item representing a single reaction.
 *  Note: length is in seconds.
 */
data class ReactionItem(
    val id: UUID,
    val userId: UUID,
    val targetVlogId: UUID,
    val dateCreated: ZonedDateTime,
    val isPrivate: Boolean,
    val length: Int?,
    val reactionStatus: ReactionStatus,
    val videoUri: Uri?,
    val thumbnailUri: Uri?
) {
    companion object ForPosting {
        /**
         *  Generates a new [ReactionItem] based on the properties
         *  required for posting a reaction. All other properties
         *  are either left at null or are set to their defaults.
         *
         *  @param id Reaction id as specified by the backend.
         *  @param targetVlogId The vlog to which this reaction should be posted.
         *  @param isPrivate Public access modifier.
         */
        fun createForPosting(id: UUID, targetVlogId: UUID, isPrivate: Boolean): ReactionItem = ReactionItem(
            id = id,
            userId = UUID(0, 0), // TODO Suboptimal, represents an empty uuid.
            targetVlogId = targetVlogId,
            dateCreated = ZonedDateTime.now(),
            isPrivate = isPrivate,
            length = null,
            reactionStatus = ReactionStatus.UP_TO_DATE,
            videoUri = null,
            thumbnailUri = null
        )
    }
}

/**
 * Map a reaction from presentation to domain.
 */
fun ReactionItem.mapToDomain(): Reaction = Reaction(
    id,
    userId,
    targetVlogId,
    dateCreated,
    isPrivate,
    length,
    reactionStatus,
    videoUri,
    thumbnailUri
)

/**
 * Map a reaction from domain to presentation.
 */
fun Reaction.mapToPresentation(): ReactionItem = ReactionItem(
    id,
    userId,
    targetVlogId,
    dateCreated,
    isPrivate,
    length,
    reactionStatus,
    videoUri,
    thumbnailUri
)

/**
 * Map a collection of reaction from domain to data.
 */
fun List<Reaction>.mapToPresentation(): List<ReactionItem> = map { it.mapToPresentation() }
