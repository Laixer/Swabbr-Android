package com.laixer.swabbr.services.notifications

import android.app.PendingIntent
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.navigation.NavDeepLinkBuilder
import com.laixer.swabbr.R
import com.laixer.swabbr.presentation.recording.StreamingFragment
import com.laixer.swabbr.presentation.recording.StreamingFragmentArgs
import com.laixer.swabbr.services.notifications.protocols.V1
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class NotificationHandler {
    private val moshi: Moshi = Moshi.Builder()
        .add(
            // We add the base class with its subclasses and the field that describes the discriminator.
            PolymorphicJsonAdapterFactory.of(V1.BaseNotification::class.java, "ClickAction")
                .withSubtype(V1.FollowedProfileLiveNotification::class.java, V1.ActionType.followed_profile_live.name)
                .withSubtype(
                    V1.FollowedProfileVlogPostedNotification::class.java,
                    V1.ActionType.followed_profile_vlog_posted.name
                )
                .withSubtype(V1.VlogGainedLikesNotification::class.java, V1.ActionType.vlog_gained_likes.name)
                .withSubtype(V1.VlogNewReactionNotification::class.java, V1.ActionType.vlog_new_reaction.name)
                .withSubtype(V1.VlogRecordRequestNotification::class.java, V1.ActionType.vlog_record_request.name)
        )
        .add(KotlinJsonAdapterFactory())
        .build()

    // Return the Notification object, created from notification payload
    fun parse(data: String): V1.BaseNotification? = moshi.adapter(V1.BaseNotification::class.java)
        .lenient()
        .fromJson(data)

    fun getPendingIntent(context: Context, notification: V1.BaseNotification?): PendingIntent? {
        // Retrieve action from notification payload or null if none exists
        val action = notification?.clickAction
        // Assign correct action if notification contains payload
        return try {
            NavDeepLinkBuilder(context).setGraph(R.navigation.nav_graph_app).setDestination(
                when (action) {
                    V1.ActionType.vlog_record_request -> R.id.streaming_dest
                    else -> R.id.streaming_dest
                }
            ).setArguments(resolveArgumentsForAction(action, notification)).createPendingIntent()
        } catch (e: IllegalArgumentException) {
            e.message?.let { Log.e(TAG, it) }
            null
        }
    }

    private fun resolveArgumentsForAction(action: V1.ActionType?, notification: V1.BaseNotification?): Bundle? =
        when (action) {
            V1.ActionType.vlog_record_request -> {
                with((notification as V1.VlogRecordRequestNotification).data) {
                    StreamingFragmentArgs(
                        StreamingFragment.StreamRequest(
                            requestMoment,
                            requestTimeout,
                            livestreamId,
                            vlogId,
                            title,
                            message
                        )
                    ).toBundle()
                }
            }
            else -> null
        }

    companion object {
        private const val TAG = "NotificationHandler"
    }
}
