package com.laixer.swabbr.services.notifications.protocols

import com.squareup.moshi.Json

class V1 {
    enum class ActionType {
        followed_profile_live,
        followed_profile_vlog_posted,
        vlog_gained_likes,
        vlog_new_reaction,
        vlog_record_request
    }

    interface BaseNotification {
        @Json(name = "Protocol")
        val protocol: String

        @Json(name = "ProtocolVersion")
        val protocolVersion: String

        @Json(name = "DataType")
        val dataType: String

        @Json(name = "DataTypeVersion")
        val dataTypeVersion: String

        @Json(name = "ClickAction")
        var clickAction: ActionType?

        @Json(name = "ContentType")
        val contentType: String

        @Json(name = "Timestamp")
        val timestamp: String

        @Json(name = "UserAgent")
        val userAgent: String

        @Json(name = "Data")
        val data: BaseNotificationData?
    }

    interface BaseNotificationData {
        @Json(name = "Title")
        val title: String

        @Json(name = "Message")
        val message: String
    }

    data class VlogRecordRequestNotification(
        override val protocol: String,
        override val protocolVersion: String,
        override val dataType: String,
        override val dataTypeVersion: String,
        override var clickAction: ActionType?,
        override val contentType: String,
        override val timestamp: String,
        override val userAgent: String,
        @Json(name = "Data") override var data: VlogRecordRequestData
    ) : BaseNotification

    data class VlogRecordRequestData(
        override val title: String,
        override val message: String,
        @Json(name = "RequestMoment") val requestMoment: String,
        @Json(name = "RequestTimeout") val requestTimeout: String,
        @Json(name = "LivestreamId") val livestreamId: String,
        @Json(name = "VlogId") val vlogId: String
    ) : BaseNotificationData

    data class FollowedProfileLiveNotification(
        override val protocol: String,
        override val protocolVersion: String,
        override val dataType: String,
        override val dataTypeVersion: String,
        override var clickAction: ActionType?,
        override val contentType: String,
        override val timestamp: String,
        override val userAgent: String,
        @Json(name = "Data") override var data: FollowedProfileLiveData
    ) : BaseNotification

    data class FollowedProfileLiveData(
        override val title: String,
        override val message: String,
        @Json(name = "LiveUserId") val liveUserId: String,
        @Json(name = "LiveVlogId") val liveVlogId: String,
        @Json(name = "LiveLivestreamId") val liveLivestreamId: String
    ) : BaseNotificationData

    data class FollowedProfileVlogPostedNotification(
        override val protocol: String,
        override val protocolVersion: String,
        override val dataType: String,
        override val dataTypeVersion: String,
        override var clickAction: ActionType?,
        override val contentType: String,
        override val timestamp: String,
        override val userAgent: String,
        @Json(name = "Data") override var data: FollowedProfileVlogPostedData
    ) : BaseNotification

    data class FollowedProfileVlogPostedData(
        override val title: String,
        override val message: String,
        @Json(name = "VlogId") val vlogId: String,
        @Json(name = "VlogOwnerUserId") val vlogOwnerUserId: String
    ) : BaseNotificationData

    data class VlogGainedLikesNotification(
        override val protocol: String,
        override val protocolVersion: String,
        override val dataType: String,
        override val dataTypeVersion: String,
        override var clickAction: ActionType?,
        override val contentType: String,
        override val timestamp: String,
        override val userAgent: String,
        @Json(name = "Data") override var data: VlogGainedLikesData
    ) : BaseNotification

    data class VlogGainedLikesData(
        override val title: String,
        override val message: String,
        @Json(name = "VlogId") val vlogId: String,
        @Json(name = "UserThatLikedId") val userThatLikedId: String
    ) : BaseNotificationData

    data class VlogNewReactionNotification(
        override val protocol: String,
        override val protocolVersion: String,
        override val dataType: String,
        override val dataTypeVersion: String,
        override var clickAction: ActionType?,
        override val contentType: String,
        override val timestamp: String,
        override val userAgent: String,
        @Json(name = "Data") override var data: VlogNewReactionData
    ) : BaseNotification

    data class VlogNewReactionData(
        override val title: String,
        override val message: String,
        @Json(name = "VlogId") val vlogId: String,
        @Json(name = "ReactionId") val reactionId: String
    ) : BaseNotificationData
}

