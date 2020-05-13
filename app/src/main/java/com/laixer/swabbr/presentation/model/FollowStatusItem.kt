package com.laixer.swabbr.presentation.model

import com.laixer.swabbr.domain.model.FollowStatus

data class FollowStatusItem(
    val status: FollowStatus?
)

fun FollowStatus.mapToPresentation(): FollowStatusItem =
    FollowStatusItem(this)

fun FollowStatusItem.mapToDomain(): FollowStatus? = this.status
