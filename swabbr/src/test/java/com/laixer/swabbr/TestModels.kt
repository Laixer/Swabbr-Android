package com.laixer.swabbr

import com.laixer.swabbr.datasource.model.*
import com.laixer.swabbr.domain.model.Login
import com.laixer.swabbr.domain.model.Reaction
import com.laixer.swabbr.domain.model.Registration
import com.laixer.swabbr.domain.model.Vlog
import com.laixer.swabbr.domain.model.User
import com.laixer.swabbr.domain.model.Settings
import com.laixer.swabbr.presentation.model.LoginItem
import com.laixer.swabbr.presentation.model.RegistrationItem
import java.util.*

private val date = Calendar.getInstance().time.toString()

val user = User(
    "userId",
    "name",
    "lastname",
    "male",
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

val login = Login(
    "username",
    "password"
)

val registration = Registration(
    "firstname",
    "lastname",
    0,
    "country",
    "email@email.com",
    "password",
    Date.from(Date().toInstant()),
    "GMT+01:00",
    "nickname",
    "profileImageUrl",
    false,
    "0612345678"
)

val loginItem = LoginItem(
    "username",
    "password"
)

val registrationItem = RegistrationItem(
    "firstname",
    "lastname",
    0,
    "country",
    "email@email.com",
    "password",
    Date.from(Date().toInstant()),
    "GMT+01:00",
    "nickname",
    "profileImageUrl",
    false,
    "0612345678"
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

val authenticatedUserEntity = AuthenticatedUserEntity(
    "token",
    userEntity,
    settingsEntity
)