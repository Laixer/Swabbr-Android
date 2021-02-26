package com.laixer.swabbr.services.notifications

import android.app.PendingIntent
import android.content.Context
import androidx.navigation.NavDeepLinkBuilder
import com.laixer.swabbr.R
import com.laixer.swabbr.presentation.reaction.WatchReactionFragmentArgs
import com.laixer.swabbr.presentation.vlogs.playback.WatchUserVlogsFragmentArgs
import com.squareup.moshi.Json
import java.time.ZonedDateTime

// TODO Clean this up, messy
//  The intent shouldn't be declared in the object.
/**
 *  V1 represents the protocol version of the notifications.
 */
class V1 {
    /**
     *  Notification base class.
     */
    data class Notification(
        @Json(name = "Protocol") val protocol: String,
        @Json(name = "ProtocolVersion") val protocolVersion: String,
        @Json(name = "NotificationAction") var notificationAction: NotificationAction,
        @Json(name = "NotificationActionString") var notificationActionString: String,
        @Json(name = "Timestamp") val timestamp: ZonedDateTime,
        @Json(name = "UserAgent") val userAgent: String,
        @Json(name = "Data") val data: NotificationData
    )

    /**
     *  Notification data base class containing all
     *  possible implementations.
     */
    sealed class NotificationData(
        @Json(name = "Title") open val title: Int,
        @Json(name = "Message") open val message: Int
    ) {
        /**
         *  Function indicating where the notification should take us.
         */
        abstract fun createPendingIntent(context: Context): PendingIntent

        /**
         *  Notification for recording a new vlog.
         */
        data class VlogRecordRequest(
            @Json(name = "RequestMoment") val requestMoment: String,
            @Json(name = "RequestTimeout") val requestTimeout: String,
            @Json(name = "VlogId") val vlogId: String
        ) : NotificationData(
            R.string.notification_title_vlogrecordrequest,
            R.string.notification_message_vlogrecordrequest
        ) {

            /**
             *  This notification will take us to the vlog recording fragment.
             */
            override fun createPendingIntent(context: Context): PendingIntent =
                NavDeepLinkBuilder(context)
                    .setGraph(R.navigation.nav_graph_main_activity)
                    .setDestination(R.id.recordVlogFragment)
                    .createPendingIntent()
        }

        /**
         *  Indicates a user that the current user is following has posted a vlog.
         */
        data class FollowedProfileVlogPosted(
            @Json(name = "VlogId") val vlogId: String,
            @Json(name = "VlogOwnerUserId") val vlogOwnerUserId: String
        ) : NotificationData(
            R.string.notification_title_followedprofilevlogposted,
            R.string.notification_message_followedprofilevlogposted
        ) {

            override fun createPendingIntent(context: Context): PendingIntent =
                NavDeepLinkBuilder(context)
                    .setGraph(R.navigation.nav_graph_main_activity)
                    .setDestination(R.id.watchUserVlogsFragment)
                    .setArguments(WatchUserVlogsFragmentArgs(vlogId, vlogOwnerUserId).toBundle())
                    .createPendingIntent()
        }

        /**
         *  Indicates one of the current users vlogs has gained a like.
         */
        data class VlogGainedLikes(
            @Json(name = "VlogId") val vlogId: String,
            @Json(name = "UserThatLikedId") val userThatLikedId: String
        ) : NotificationData(
            R.string.notification_title_vloggainedlikes,
            R.string.notification_message_vloggainedlikes
        ) {

            override fun createPendingIntent(context: Context): PendingIntent =
                NavDeepLinkBuilder(context)
                    .setGraph(R.navigation.nav_graph_main_activity)
                    .setDestination(R.id.watchUserVlogsFragment)
                    .setArguments(WatchUserVlogsFragmentArgs(vlogId, userThatLikedId).toBundle())
                    .createPendingIntent()
        }

        /**
         *  Indicates a reaction was posted to one of the current users vlogs.
         */
        data class VlogNewReaction(
            @Json(name = "VlogId") val vlogId: String,
            @Json(name = "ReactionId") val reactionId: String
        ) : NotificationData(
            R.string.notification_title_vlognewreaction,
            R.string.notification_message_vlognewreaction
        ) {

            override fun createPendingIntent(context: Context): PendingIntent =
                NavDeepLinkBuilder(context)
                    .setGraph(R.navigation.nav_graph_main_activity)
                    .setDestination(R.id.watchReactionFragment)
                    .setArguments(WatchReactionFragmentArgs(reactionId).toBundle())
                    .createPendingIntent()
        }
    }
}
