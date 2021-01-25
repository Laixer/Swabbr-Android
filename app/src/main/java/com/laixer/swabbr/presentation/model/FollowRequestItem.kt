package com.laixer.swabbr.presentation.model

import com.laixer.swabbr.domain.model.FollowRequest
import com.laixer.swabbr.domain.types.FollowRequestStatus
import java.time.ZonedDateTime
import java.util.*

// TODO Is it correct to use domain types here?

/**
 *  Item representing a follow request between two users.
 */
data class FollowRequestItem(
    val requesterId: UUID,
    val receiverId: UUID,
    var requestStatus: FollowRequestStatus,
    val timeCreated: ZonedDateTime?
)

/**
 *  Map a follow request from domain to presentation.
 */
fun FollowRequest.mapToPresentation(): FollowRequestItem =
    FollowRequestItem(
        this.requesterId,
        this.receiverId,
        this.followRequestStatus,
        this.dateCreated
    )

/**
 *  Map a collection of follow requests from domain to presentation.
 */
fun List<FollowRequest>.mapToPresentation(): List<FollowRequestItem> = map { it.mapToPresentation() }
