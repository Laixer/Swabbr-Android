package com.laixer.swabbr.services.notifications.protocols

import com.squareup.moshi.Json

class V1 {
    open class BaseNotificationPayload(
        @field:Json(name = "title") open val title: String,
        @field:Json(name = "message") open val message: String
    )

    class FollowedProfileLivePayload(
        @field:Json(name = "title") override val title: String,
        @field:Json(name = "message") override val message: String,
        @field:Json(name = "liveUserId") val liveUserId: String,
        @field:Json(name = "liveVlogId") val liveVlogId: String,
        @field:Json(name = "liveLivestreamId") val liveLivestreamId: String
    ) : BaseNotificationPayload(title, message)

    class FollowedProfileVlogPostedPayload(
        @field:Json(name = "title") override val title: String,
        @field:Json(name = "message") override val message: String,
        @field:Json(name = "vlogId") val vlogId: String,
        @field:Json(name = "vlogOwnerUserId") val vlogOwnerUserId: String
    ) : BaseNotificationPayload(title, message)

    class VlogGainedLikesPayload(
        @field:Json(name = "title") override val title: String,
        @field:Json(name = "message") override val message: String,
        @field:Json(name = "vlogId") val vlogId: String,
        @field:Json(name = "userThatLikedId") val userThatLikedId: String
    ) : BaseNotificationPayload(title, message)

    class VlogNewReactionPayload(
        @field:Json(name = "title") override val title: String,
        @field:Json(name = "message") override val message: String,
        @field:Json(name = "vlogId") val vlogId: String,
        @field:Json(name = "reactionId") val reactionId: String
    ) : BaseNotificationPayload(title, message)

    class VlogRecordRequestPayload(
        @field:Json(name = "title") override val title: String,
        @field:Json(name = "message") override val message: String,
        @field:Json(name = "requestMoment") val requestMoment: String,
        @field:Json(name = "requestTimeout") val requestTimeout: String,
        @field:Json(name = "livestreamId") val livestreamId: String,
        @field:Json(name = "vlogId") val vlogId: String
    ) : BaseNotificationPayload(title, message)
}
