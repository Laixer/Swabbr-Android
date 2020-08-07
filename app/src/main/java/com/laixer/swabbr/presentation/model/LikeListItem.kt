package com.laixer.swabbr.presentation.model

import com.laixer.swabbr.domain.model.LikeList
import com.laixer.swabbr.domain.model.MinifiedUser
import java.util.UUID

data class LikeListItem(val totalLikeCount: Int, val usersMinified: List<MinifiedUserItem>)

data class MinifiedUserItem(val id: UUID, val nickname: String)

fun LikeList.mapToPresentation(): LikeListItem = LikeListItem(this.totalLikeCount, this.usersMinified.mapToPresentation())

fun MinifiedUser.mapToPresentation(): MinifiedUserItem = MinifiedUserItem(this.id, this.nickname)

fun List<MinifiedUser>.mapToPresentation(): List<MinifiedUserItem> = map { it.mapToPresentation() }
