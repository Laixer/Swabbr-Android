package com.laixer.swabbr.services.notifications

import android.app.PendingIntent
import android.content.Context
import androidx.navigation.NavDeepLinkBuilder
import com.laixer.swabbr.R
import com.laixer.swabbr.presentation.livestream.LivestreamFragmentArgs
import com.laixer.swabbr.presentation.vlogs.details.VlogDetailsFragmentArgs
import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.lang.reflect.Type
import java.time.ZonedDateTime

class V1 {

    @Suppress("UNUSED")
    enum class NotificationType(val derivedClass: Class<out NotificationData>) {

        followed_profile_live(NotificationData.FollowedProfileLive::class.java),
        followed_profile_vlog_posted(NotificationData.FollowedProfileVlogPosted::class.java),
        vlog_gained_likes(NotificationData.VlogGainedLikes::class.java),
        vlog_new_reaction(NotificationData.VlogNewReaction::class.java),
        vlog_record_request(NotificationData.VlogRecordRequest::class.java)
    }

    data class Notification(
        @Json(name = "Protocol") val protocol: String,
        @Json(name = "ProtocolVersion") val protocolVersion: String,
        @Json(name = "DataType") val dataType: String,
        @Json(name = "DataTypeVersion") val dataTypeVersion: String,
        @Json(name = "ClickAction") var clickAction: NotificationType,
        @Json(name = "ContentType") val contentType: String,
        @Json(name = "Timestamp") val timestamp: ZonedDateTime,
        @Json(name = "UserAgent") val userAgent: String,
        @Json(name = "Data") val data: NotificationData
    )

    sealed class NotificationData(
        @Json(name = "Title") open val title: String,
        @Json(name = "Message") open val message: String
    ) {

        abstract fun createPendingIntent(context: Context): PendingIntent

        data class VlogRecordRequest(
            @Json(name = "Title") override val title: String = "It's time to vlog!",
            @Json(name = "Message") override val message: String = "Don't let your followers wait, click here to start streaming.",
            @Json(name = "RequestMoment") val requestMoment: String,
            @Json(name = "RequestTimeout") val requestTimeout: String,
            @Json(name = "LivestreamId") val livestreamId: String,
            @Json(name = "VlogId") val vlogId: String
        ) : NotificationData(title, message) {

            override fun createPendingIntent(context: Context): PendingIntent = NavDeepLinkBuilder(context)
                .setGraph(R.navigation.nav_graph_app)
                .setDestination(R.id.streaming_dest)
                .setArguments(LivestreamFragmentArgs(livestreamId).toBundle()).createPendingIntent()
        }

        data class FollowedProfileLive(
            @Json(name = "Title") override val title: String = "One of your friends just started streaming!",
            @Json(name = "Message") override val message: String = "Click here to catch them live.",
            @Json(name = "LiveUserId") val liveUserId: String,
            @Json(name = "LiveVlogId") val liveVlogId: String,
            @Json(name = "LiveLivestreamId") val liveLivestreamId: String
        ) : NotificationData(title, message) {

            override fun createPendingIntent(context: Context): PendingIntent =
                NavDeepLinkBuilder(context)
                    .setGraph(R.navigation.nav_graph_vlogs)
                    .setDestination(R.id.vlog_details_dest)
                    .setArguments(VlogDetailsFragmentArgs(liveVlogId, liveUserId, liveLivestreamId).toBundle())
                    .createPendingIntent()
        }

        data class FollowedProfileVlogPosted(
            @Json(name = "Title") override val title: String = "Your friend just posted a new vlog!",
            @Json(name = "Message") override val message: String = "Click here and be the first to see it.",
            @Json(name = "VlogId") val vlogId: String,
            @Json(name = "VlogOwnerUserId") val vlogOwnerUserId: String
        ) : NotificationData(title, message) {

            override fun createPendingIntent(context: Context): PendingIntent =
                NavDeepLinkBuilder(context)
                    .setGraph(R.navigation.nav_graph_vlogs)
                    .setDestination(R.id.vlog_details_dest)
                    .setArguments(VlogDetailsFragmentArgs(vlogId, vlogOwnerUserId, null).toBundle())
                    .createPendingIntent()
        }

        data class VlogGainedLikes(
            @Json(name = "Title") override val title: String = "Your vlog is getting popular!",
            @Json(name = "Message") override val message: String = "You just received some new likes, check it out.",
            @Json(name = "VlogId") val vlogId: String,
            @Json(name = "UserThatLikedId") val userThatLikedId: String
        ) : NotificationData(title, message) {

            override fun createPendingIntent(context: Context): PendingIntent =
                NavDeepLinkBuilder(context)
                    .setGraph(R.navigation.nav_graph_vlogs)
                    .setDestination(R.id.vlog_details_dest)
                    .setArguments(VlogDetailsFragmentArgs(vlogId, null, null).toBundle())
                    .createPendingIntent()
        }

        data class VlogNewReaction(
            @Json(name = "Title") override val title: String = "People are talking about you!",
            @Json(name = "Message") override val message: String = "Click here to see their reactions.",
            @Json(name = "VlogId") val vlogId: String,
            @Json(name = "ReactionId") val reactionId: String
        ) : NotificationData(title, message) {

            // TODO: Actually direct to the specified reaction
            override fun createPendingIntent(context: Context): PendingIntent =
                NavDeepLinkBuilder(context)
                    .setGraph(R.navigation.nav_graph_vlogs)
                    .setDestination(R.id.vlog_details_dest)
                    .setArguments(VlogDetailsFragmentArgs(vlogId, null, null).toBundle())
                    .createPendingIntent()
        }
    }
}

class NotificationFactory : JsonAdapter.Factory {

    override fun create(type: Type, annotations: MutableSet<out Annotation>, moshi: Moshi): JsonAdapter<*>? {
        if (!Types.getRawType(type).isAssignableFrom(V1.Notification::class.java)) {
            return null
        }

        return object : JsonAdapter<V1.Notification>() {
            private val dataTypeAdapter = moshi.adapter<V1.NotificationType>(
                V1.NotificationType::class.java
            )

            override fun fromJson(reader: JsonReader): V1.Notification? {
                val jsonValue = reader.readJsonValue()

                @Suppress("UNCHECKED_CAST")
                val value = jsonValue as Map<String, Any>
                val clickAction = dataTypeAdapter.fromJsonValue(jsonValue["ClickAction"])!!
                val data = moshi.adapter(clickAction.derivedClass).fromJsonValue(value["Data"])
                    ?: throw JsonDataException()

                return V1.Notification(
                    protocol = value["Protocol"] as String,
                    protocolVersion = value["ProtocolVersion"] as String,
                    dataType = value["DataType"] as String,
                    dataTypeVersion = value["DataTypeVersion"] as String,
                    clickAction = clickAction,
                    contentType = value["ContentType"] as String,
                    timestamp = ZonedDateTime.parse(value["Timestamp"] as String),
                    userAgent = value["UserAgent"] as String,
                    data = data
                )
            }

            override fun toJson(writer: JsonWriter, value: V1.Notification?) {
                TODO("Not yet implemented")
            }
        }
    }
}
