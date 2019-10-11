package com.laixer.sample.presentation.model

import com.laixer.sample.domain.model.User
import com.laixer.sample.domain.model.Vlog

data class ProfileItem(
    val user: User,
    val vlogs: List<VlogItem>
)

fun Pair<User, List<Vlog>>.mapToPresentation(): ProfileItem =
    ProfileItem(this.first, this.second.map { it.mapToPresentation(this.first) })