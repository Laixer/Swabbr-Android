package com.laixer.sample.presentation.model

import com.laixer.sample.domain.model.Vlog

data class ProfileItem(
    val userName: String,
    val totalVlogs: Int,
    val totalFollowers: Int,
    val totalFollowing: Int,
    val Vlogs: List<Vlog>
)