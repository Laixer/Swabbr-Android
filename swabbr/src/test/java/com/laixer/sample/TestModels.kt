package com.laixer.swabbr

import com.laixer.swabbr.datasource.model.*
import com.laixer.swabbr.domain.model.Reaction
import com.laixer.swabbr.domain.model.Vlog
import com.laixer.swabbr.domain.model.User
import com.laixer.swabbr.domain.model.Settings
import java.util.*

private val date = Calendar.getInstance().time.toString()

val user = User(
    "userId",
    "name",
    "username",
    "email",
    "country",
    "email",
    "timezone",
    0,
    0,
    0,
    "nickname",
    "profileImgeUrl",
    "birthdate",
    0.0,
    0.0
)
val vlog = Vlog(
    "userId",
    "id",
    "duration",
    date,
    0,
    0,
    0,
    isLive = false,
    isPrivate = false
)

val reaction = Reaction(
    "userId",
    "vlogId",
    "id",
    "duration",
    date
)

val settings = Settings(
    false,
    2,
    1
)

val followStatus = "Pending"

val pairUserVlog = Pair(user, vlog)

val userEntity = UserEntity(
    "userId",
    "name",
    "username",
    "email",
    "country",
    "email",
    "timezone",
    0,
    0,
    0,
    "nickname",
    "profileImgeUrl",
    "birthdate",
    0.0,
    0.0
)
val vlogEntity = VlogEntity(
    "userId",
    "id",
    "duration",
    date,
    0,
    0,
    0,
    isLive = false,
    isPrivate = false
)
val reactionEntity = ReactionEntity(
    "userId",
    "vlogId",
    "id",
    "duration",
    date
)

val settingsEntity = SettingsEntity(
    false,
    2,
    2
)

val followStatusEntity = FollowStatusEntity(
    "pending"
)