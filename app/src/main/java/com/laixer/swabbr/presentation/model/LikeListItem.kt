package com.laixer.swabbr.presentation.model

import com.laixer.swabbr.domain.model.LikeList
import com.laixer.swabbr.domain.model.SimplifiedUser
import java.util.UUID

data class LikeListItem(val totalLikeCount: Int, val usersSimplified: List<SimplifiedUserItem>)

data class SimplifiedUserItem(val id: UUID, val nickname: String)

fun LikeList.mapToPresentation(): LikeListItem = LikeListItem(this.totalLikeCount, this.usersSimplified.mapToPresentation())

fun List<SimplifiedUser>.mapToPresentation(): List<SimplifiedUserItem> = map { it.mapToPresentation() }
fun SimplifiedUser.mapToPresentation(): SimplifiedUserItem = SimplifiedUserItem(this.id, this.nickname)
