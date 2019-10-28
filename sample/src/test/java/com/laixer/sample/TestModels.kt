package com.laixer.sample

import com.laixer.sample.datasource.model.*
import com.laixer.sample.domain.model.Reaction
import com.laixer.sample.domain.model.Vlog
import com.laixer.sample.domain.model.User
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
