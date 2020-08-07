package com.laixer.swabbr.domain.model

import com.laixer.swabbr.presentation.model.MinifiedUserItem
import java.time.ZonedDateTime
import java.util.UUID

data class LikeList(val totalLikeCount: Int, val usersMinified: List<MinifiedUser>)

data class MinifiedUser(val id: UUID, val nickname: String)
