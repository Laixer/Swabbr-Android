package com.laixer.swabbr

import com.laixer.swabbr.datasource.model.*
import com.laixer.swabbr.domain.model.Like
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

val like = Like(
    "likeId",
    "vlogId",
    "userId",
    "time"
)

val vlog = Vlog(
    "userId",
    "id",
    false,
    false,
    "startdate",
    listOf(like)
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
    "password",
    true
)

val registration = Registration(
    "firstname",
    "lastname",
    0,
    "country",
    "email@email.com",
    "password",
    "T11:48:52.844Z",
    "GMT+01:00",
    "nickname",
    "profileImageUrl",
    false,
    "0612345678"
)

val loginItem = LoginItem(
    "username",
    "password",
    true
)

val registrationItem = RegistrationItem(
    "firstname",
    "lastname",
    0,
    "country",
    "email@email.com",
    "password",
    "T11:48:52.844Z",
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
    "T11:48:52.844Z",
    0.0,
    0.0
)

val likeEntity = LikeEntity(
    "likeId",
    "vlogId",
    "userId",
    "time"
)

val vlogEntity = VlogEntity(
    "id",
    "userId",
    false,
    false,
    "startdate",
    listOf(likeEntity)
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

val followStatusEntity = FollowRequestEntity(
    "pending"
)

val authenticatedUserEntity = AuthUserEntity(
    "token",
    userEntity,
    settingsEntity
)

val loginEntity = LoginEntity(
    "username",
    "password",
    true
)

val registrationEntity = RegistrationEntity(
    "firstname",
    "lastname",
    0,
    "country",
    "email@email.com",
    "password",
    "T11:48:52.844Z",
    "GMT+01:00",
    "nickname",
    "profileImageUrl",
    false,
    "0612345678"
)
