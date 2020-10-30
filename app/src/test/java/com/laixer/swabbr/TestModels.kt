package com.laixer.swabbr

import com.laixer.swabbr.data.datasource.model.*
import com.laixer.swabbr.domain.model.*
import com.laixer.swabbr.presentation.model.*
import com.laixer.swabbr.services.notifications. ActionType
import com.laixer.swabbr.services.notifications.protocols.BaseNotification
import com.laixer.swabbr.services.notifications.V1
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

val userId: UUID = UUID.randomUUID()
val vlogId: UUID = UUID.randomUUID()
val likeId: UUID = UUID.randomUUID()
val reactionId: UUID = UUID.randomUUID()
val followRequestId: UUID = UUID.randomUUID()

object Models {
    val followStatus = FollowStatus.NOT_FOLLOWING
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
        profileImage = "base64string",
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
        dateStarted = ZonedDateTime.parse("2020-03-09T12:33:55.640Z"),
        views = 0
    )
    val reaction = Reaction(
        id = reactionId,
        userId = userId,
        vlogId = vlogId,
        datePosted = ZonedDateTime.parse("2020-03-09T12:34:47.236Z"),
        isPrivate = false
    )
    val settings = Settings(
        private = false,
        dailyVlogRequestLimit = 2,
        followMode = FollowMode.MANUAL
    )
    val followRequest = FollowRequest(
        requesterId = userId,
        receiverId = userId,
        status = FollowStatus.FOLLOWING,
        timeCreated = ZonedDateTime.parse("2020-03-09T12:36:02.171Z")
    )
    val login = Login(
        email = "username",
        password = "password",
        remember = true,
        handle = "handle",
        pushNotificationPlatform = PushNotificationPlatform.FCM
    )
    val registration = Registration(
//        firstName = "firstname",
//        register_lastName = "lastname",
//        gender = Gender.MALE,
//        country = "country",
        email = "email@email.com",
        password = "password",
//        birthdate = ZonedDateTime.parse("2020-03-09T12:38:06.643Z"),
        timezone = ZoneOffset.UTC,
        nickname = "nickname",
        profileImage = "base64string",
//        phoneNumber = "0612345678",
        handle = "handle",
        pushNotificationPlatform = PushNotificationPlatform.FCM
    )
    val authUser = AuthUser(
        jwtToken = "token",
        user = user,
        userSettings = settings
    )
}

object Items {
    val followStatus = FollowStatusItem(
        Models.followStatus
    )
    val login = LoginItem(
        Models.login.email,
        Models.login.password,
        Models.login.remember,
        Models.login.pushNotificationPlatform,
        Models.login.handle
    )
    val registration = RegistrationItem(
//        Models.registration.firstName,
//        Models.registration.register_lastName,
//        Models.registration.gender,
//        Models.registration.country,
        Models.registration.email,
        Models.registration.password,
//        Models.registration.birthdate,
        Models.registration.timezone,
        Models.registration.nickname,
        Models.registration.profileImage,
//        Models.registration.phoneNumber,
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
        Models.user.profileImage,
        Models.user.birthdate
    )
    val settings = SettingsItem(
        Models.settings.private,
        Models.settings.dailyVlogRequestLimit,
        Models.settings.followMode
    )
    val authUser = AuthUserItem(
        Models.authUser.jwtToken,
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
//        Models.followRequest.id,
        Models.followRequest.requesterId,
        Models.followRequest.receiverId,
        Models.followRequest.status,
        Models.followRequest.timeCreated
    )
    val reaction = ReactionUserItem(
        Models.reaction.id,
        Models.reaction.userId,
        Models.reaction.vlogId,
        Models.user.nickname,
        Models.user.profileImage,
        Models.reaction.datePosted
    )
    val uservlog = UserVlogItem(
        Models.user.id,
        Models.user.nickname,
        Models.user.firstName,
        Models.user.lastName,
        Models.user.profileImage,
        Models.vlog.id,
        Models.vlog.dateStarted,
        Models.vlog.views
    )
    val vlog = VlogItem(
        Models.vlog.id,
        Models.vlog.userId,
        Models.vlog.isPrivate,
        Models.vlog.dateStarted,
        Models.vlog.views
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
        Models.user.profileImage,
        Models.user.birthdate?.atStartOfDay()?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
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
        Models.vlog.isPrivate,
        Models.vlog.dateStarted.toString(),
        Models.vlog.views
    )
    val reaction = ReactionEntity(
        Models.reaction.id.toString(),
        Models.reaction.userId.toString(),
        Models.reaction.vlogId.toString(),
        Models.reaction.datePosted.toString(),
        Models.reaction.isPrivate
    )
    val settings = SettingsEntity(
        Models.settings.private,
        Models.settings.dailyVlogRequestLimit,
        Models.settings.followMode.value
    )
    val followRequest = FollowRequestEntity(
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
        Models.login.email,
        Models.login.password,
        Models.login.remember,
        Models.login.pushNotificationPlatform.value,
        Models.login.handle
    )
    val registration = RegistrationEntity(
//        Models.registration.firstName,
//        Models.registration.register_lastName,
//        Models.registration.gender.value,
//        Models.registration.country,
        Models.registration.email,
        Models.registration.password,
//        Models.registration.birthdate.toInstant().toString(),
        "UTC${DateTimeFormatter.ofPattern("xxx").format(Models.registration.timezone)}",
        Models.registration.nickname,
        Models.registration.profileImage,
//        Models.registration.phoneNumber,
        Models.registration.pushNotificationPlatform.value,
        Models.registration.handle
    )
}

object Notifications {
    val baseNotification = BaseNotification(
        protocol = "swabbr",
        protocolVersion = "1.0",
        dataType = "test",
        dataTypeVersion = "1.0",
        clickAction = null,
        contentType = "json",
        timestamp = "2020-05-11T12:01:36.440Z",
        userAgent = "agent",
        data = null
    )
    val followed_profile_live = BaseNotification(
        protocol = "swabbr",
        protocolVersion = "1.0",
        dataType = "test",
        dataTypeVersion = "1.0",
        clickAction = ActionType.FOLLOWED_PROFILE_LIVE,
        contentType = "json",
        timestamp = "2020-05-11T12:01:36.440Z",
        userAgent = "agent",
        data = V1.FollowedProfileLiveData(
            liveUserId = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
            liveLivestreamId = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
            liveVlogId = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
            title = "A followed profile has gone live!",
            message = "Click here to view the livestream"
        )
    )
    val followed_profile_vlog_posted = BaseNotification(
        protocol = "swabbr",
        protocolVersion = "1.0",
        dataType = "test",
        dataTypeVersion = "1.0",
        clickAction = ActionType.FOLLOWED_PROFILE_VLOG_POSTED,
        contentType = "json",
        timestamp = "2020-05-11T12:01:36.440Z",
        userAgent = "agent",
        data = V1.FollowedProfileVlogPostedData(
            vlogId = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
            vlogOwnerUserId = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
            title = "Someone posted a new vlog!",
            message = "Click here to view the vlog"
        )
    )

    val vlog_record_request = BaseNotification(
        protocol = "swabbr",
        protocolVersion = "1.0",
        dataType = "test",
        dataTypeVersion = "1.0",
        clickAction = ActionType.VLOG_RECORD_REQUEST,
        contentType = "json",
        timestamp = "2020-05-11T12:01:36.440Z",
        userAgent = "agent",
        data = V1.VlogRecordRequestData(
            requestMoment = "2020-05-11T12:05:53.451Z",
            requestTimeout = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
            livestreamId = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
            vlogId = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
            title = "You should reecord a new vlog",
            message = "Click here to start recording"
        )
    )

    val vlog_gained_likes = BaseNotification(
        protocol = "swabbr",
        protocolVersion = "1.0",
        dataType = "test",
        dataTypeVersion = "1.0",
        clickAction = ActionType.VLOG_GAINED_LIKES,
        contentType = "json",
        timestamp = "2020-05-11T12:01:36.440Z",
        userAgent = "agent",
        data = V1.VlogGainedLikesData(
            vlogId = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
            userThatLikedId = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
            title = "Your vlog gained new likes!",
            message = "Click to seee who liked your vlog"
        )
    )
    val vlog_new_reaction = BaseNotification(
        protocol = "swabbr",
        protocolVersion = "1.0",
        dataType = "test",
        dataTypeVersion = "1.0",
        clickAction = ActionType.VLOG_NEW_REACTION,
        contentType = "json",
        timestamp = "2020-05-11T12:01:36.440Z",
        userAgent = "agent",
        data = V1.VlogNewReactionData(
            vlogId = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
            reactionId = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
            title = "Your vlog has a new reaction",
            message = "Click to see it!"
        )
    )
}
