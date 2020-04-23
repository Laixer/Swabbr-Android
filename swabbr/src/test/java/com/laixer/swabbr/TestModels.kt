package com.laixer.swabbr

import com.laixer.swabbr.datasource.model.AuthUserEntity
import com.laixer.swabbr.datasource.model.FollowRequestEntity
import com.laixer.swabbr.datasource.model.LikeEntity
import com.laixer.swabbr.datasource.model.LoginEntity
import com.laixer.swabbr.datasource.model.ReactionEntity
import com.laixer.swabbr.datasource.model.RegistrationEntity
import com.laixer.swabbr.datasource.model.SettingsEntity
import com.laixer.swabbr.datasource.model.UserEntity
import com.laixer.swabbr.datasource.model.VlogEntity
import com.laixer.swabbr.domain.model.AuthUser
import com.laixer.swabbr.domain.model.FollowMode
import com.laixer.swabbr.domain.model.FollowRequest
import com.laixer.swabbr.domain.model.FollowStatus
import com.laixer.swabbr.domain.model.Gender
import com.laixer.swabbr.domain.model.Like
import com.laixer.swabbr.domain.model.Login
import com.laixer.swabbr.domain.model.PushNotificationPlatform
import com.laixer.swabbr.domain.model.Reaction
import com.laixer.swabbr.domain.model.Registration
import com.laixer.swabbr.domain.model.Settings
import com.laixer.swabbr.domain.model.User
import com.laixer.swabbr.domain.model.Vlog
import com.laixer.swabbr.presentation.model.AuthUserItem
import com.laixer.swabbr.presentation.model.FollowRequestItem
import com.laixer.swabbr.presentation.model.LikeItem
import com.laixer.swabbr.presentation.model.LoginItem
import com.laixer.swabbr.presentation.model.ReactionItem
import com.laixer.swabbr.presentation.model.RegistrationItem
import com.laixer.swabbr.presentation.model.SettingsItem
import com.laixer.swabbr.presentation.model.UserItem
import com.laixer.swabbr.presentation.model.UserVlogItem
import com.laixer.swabbr.presentation.model.VlogItem
import java.net.URL
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.TimeZone
import java.util.UUID

val userId: UUID = UUID.randomUUID()
val vlogId: UUID = UUID.randomUUID()
val likeId: UUID = UUID.randomUUID()
val reactionId: UUID = UUID.randomUUID()
val followRequestId: UUID = UUID.randomUUID()

object Models {
    val user = User(
        id = userId,
        firstName = "name",
        lastName = "lastname",
        gender = Gender.MALE,
        country = "country",
        email = "email",
        timezone = TimeZone.getDefault(),
        totalVlogs = 0,
        totalFollowers = 0,
        totalFollowing = 0,
        nickname = "nickname",
        profileImageUrl = URL("https://sample-profileImage-url.com/"),
        birthdate = LocalDate.parse("1996-06-13")
    )
    val like = Like(
        id = likeId,
        vlogId = vlogId,
        userId = userId,
        timeCreated = ZonedDateTime.parse("2020-02-09T11:03:12.832Z")
    )
    val vlog = Vlog(
        id = vlogId,
        userId = userId,
        isPrivate = false,
        isLive = false,
        dateStarted = ZonedDateTime.parse("2020-03-09T12:33:55.640Z"),
        likes = listOf(like),
        url = URL("https://sample-vlog-url.com/"),
        totalReactions = 50,
        totalViews = 20
    )
    val reaction = Reaction(
        id = reactionId,
        userId = userId,
        vlogId = vlogId,
        datePosted = ZonedDateTime.parse("2020-03-09T12:34:47.236Z")
    )
    val settings = Settings(
        private = false,
        dailyVlogRequestLimit = 2,
        followMode = FollowMode.MANUAL
    )
    val followRequest = FollowRequest(
        id = followRequestId,
        requesterId = userId,
        receiverId = userId,
        status = FollowStatus.FOLLOWING,
        timeCreated = ZonedDateTime.parse("2020-03-09T12:36:02.171Z")
    )
    val login = Login(
        username = "username",
        password = "password",
        remember = true,
        handle = "handle",
        pushNotificationPlatform = PushNotificationPlatform.FCM
    )
    val registration = Registration(
        firstName = "firstname",
        lastName = "lastname",
        gender = Gender.MALE,
        country = "country",
        email = "email@email.com",
        password = "password",
        birthdate = ZonedDateTime.parse("2020-03-09T12:38:06.643Z"),
        timezone = ZoneOffset.UTC,
        nickname = "nickname",
        profileImageUrl = URL("https://sample-profileImage-url.com/"),
        phoneNumber = "0612345678",
        handle = "handle",
        pushNotificationPlatform = PushNotificationPlatform.FCM
    )
    val authUser = AuthUser(
        accessToken = "token",
        user = user,
        userSettings = settings
    )
}

object Items {
    val login = LoginItem(
        Models.login.username,
        Models.login.password,
        Models.login.remember,
        Models.login.pushNotificationPlatform,
        Models.login.handle
    )
    val registration = RegistrationItem(
        Models.registration.firstName,
        Models.registration.lastName,
        Models.registration.gender,
        Models.registration.country,
        Models.registration.email,
        Models.registration.password,
        Models.registration.birthdate,
        Models.registration.timezone,
        Models.registration.nickname,
        Models.registration.profileImageUrl,
        Models.registration.phoneNumber,
        Models.registration.pushNotificationPlatform,
        Models.registration.handle
    )
    val user = UserItem(
        Models.user.id,
        Models.user.firstName,
        Models.user.lastName,
        Models.user.gender,
        Models.user.country,
        Models.user.email,
        Models.user.timezone,
        Models.user.totalVlogs,
        Models.user.totalFollowers,
        Models.user.totalFollowing,
        Models.user.nickname,
        Models.user.profileImageUrl,
        Models.user.birthdate
    )
    val settings = SettingsItem(
        Models.settings.private,
        Models.settings.dailyVlogRequestLimit,
        Models.settings.followMode
    )
    val authUser = AuthUserItem(
        Models.authUser.accessToken,
        settings,
        user
    )
    val like = LikeItem(
        Models.like.id,
        Models.like.vlogId,
        Models.like.userId,
        Models.like.timeCreated
    )
    val followRequest = FollowRequestItem(
        Models.followRequest.id,
        Models.followRequest.requesterId,
        Models.followRequest.receiverId,
        Models.followRequest.status,
        Models.followRequest.timeCreated
    )
    val reaction = ReactionItem(
        Models.reaction.id,
        Models.reaction.userId,
        Models.reaction.vlogId,
        Models.user.nickname,
        Models.user.profileImageUrl,
        Models.reaction.datePosted
    )
    val uservlog = UserVlogItem(
        Models.user.id,
        Models.user.nickname,
        Models.user.firstName,
        Models.user.lastName,
        Models.user.profileImageUrl,
        Models.vlog.id,
        Models.vlog.dateStarted,
        Models.vlog.isLive,
        Models.vlog.totalViews,
        Models.vlog.totalReactions,
        Models.vlog.likes.size,
        Models.vlog.url
    )
    val vlog = VlogItem(
        Models.vlog.id,
        Models.vlog.userId,
        Models.vlog.dateStarted,
        Models.vlog.isLive,
        Models.vlog.likes.size,
        Models.vlog.url
    )
}

object Entities {
    val user = UserEntity(
        Models.user.id.toString(),
        Models.user.firstName,
        Models.user.lastName,
        Models.user.email,
        Models.user.country,
        Models.user.gender.value,
        Models.user.timezone.id,
        Models.user.totalVlogs,
        Models.user.totalFollowers,
        Models.user.totalFollowing,
        Models.user.nickname,
        Models.user.profileImageUrl.toString(),
        Models.user.birthdate.atStartOfDay().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    )
    val like = LikeEntity(
        Models.like.id.toString(),
        Models.like.vlogId.toString(),
        Models.like.userId.toString(),
        Models.like.timeCreated.toString()
    )
    val vlog = VlogEntity(
        Models.vlog.id.toString(),
        Models.vlog.userId.toString(),
        Models.vlog.url.toString(),
        Models.vlog.isPrivate,
        Models.vlog.isLive,
        Models.vlog.dateStarted.toString(),
        Models.vlog.totalViews,
        Models.vlog.totalReactions,
        listOf(like)
    )
    val reaction = ReactionEntity(
        Models.reaction.id.toString(),
        Models.reaction.userId.toString(),
        Models.reaction.vlogId.toString(),
        Models.reaction.datePosted.toString()
    )
    val settings = SettingsEntity(
        Models.settings.private,
        Models.settings.dailyVlogRequestLimit,
        Models.settings.followMode.value
    )
    val followRequest = FollowRequestEntity(
        Models.followRequest.id.toString(),
        Models.followRequest.requesterId.toString(),
        Models.followRequest.requesterId.toString(),
        Models.followRequest.status.value,
        Models.followRequest.timeCreated.toString()
    )
    val authUser = AuthUserEntity(
        "token",
        user,
        settings
    )
    val login = LoginEntity(
        Models.login.username,
        Models.login.password,
        Models.login.remember,
        Models.login.pushNotificationPlatform.value,
        Models.login.handle
    )
    val registration = RegistrationEntity(
        Models.registration.firstName,
        Models.registration.lastName,
        Models.registration.gender.value,
        Models.registration.country,
        Models.registration.email,
        Models.registration.password,
        Models.registration.birthdate.toInstant().toString(),
        "UTC${DateTimeFormatter.ofPattern("xxx").format(Models.registration.timezone)}",
        Models.registration.nickname,
        Models.registration.profileImageUrl.toString(),
        Models.registration.phoneNumber,
        Models.registration.pushNotificationPlatform.value,
        Models.registration.handle
    )
}
