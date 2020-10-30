package com.laixer.swabbr.domain.model

import android.net.Uri
import java.time.ZonedDateTime
import java.util.UUID

data class Vlog(
    val data: VlogData,
    val vlogLikeSummary: VlogLikeSummary,
    val thumbnailUri: Uri?
)

data class VlogData(
    val id: UUID,
    val userId: UUID,
    val isPrivate: Boolean,
    val dateStarted: ZonedDateTime,
    val views: Int
) {

    fun equals(compare: VlogData): Boolean =
        this.id == compare.id && this.userId == compare.userId && this.dateStarted == compare.dateStarted && this.views == compare.views

}

data class VlogLikeSummary(
    val vlogId: UUID,
    val totalLikes: Int,
    val simplifiedUsers: List<SimplifiedUser>
) {

    // We just compare simplifiedUsers size to be more efficient.
    // Comparing  all values will be very expensive
    fun equals(compare: VlogLikeSummary): Boolean =
        this.vlogId == compare.vlogId && this.totalLikes == compare.totalLikes && this.simplifiedUsers.size == compare.simplifiedUsers.size
}
