package com.laixer.swabbr.services.notifications

import android.content.Context
import android.content.Intent
import android.util.Log
import com.laixer.swabbr.presentation.AppActivity
import com.laixer.swabbr.services.notifications.protocols.BaseNotification
import com.laixer.swabbr.services.notifications.protocols.V1

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

enum class ActionType {
    FOLLOWED_PROFILE_LIVE,
    FOLLOWED_PROFILE_VLOG_POSTED,
    VLOG_GAINED_LIKES,
    VLOG_NEW_REACTION,
    VLOG_RECORD_REQUEST
}

class NotificationHandler {
    private val moshi: Moshi = Moshi.Builder()
        .add(
            PolymorphicJsonAdapterFactory.of(V1.BaseNotificationPayload::class.java, "clickAction")
                .withSubtype(V1.FollowedProfileLivePayload::class.java, ActionType.FOLLOWED_PROFILE_LIVE.name)
                .withSubtype(
                    V1.FollowedProfileVlogPostedPayload::class.java,
                    ActionType.FOLLOWED_PROFILE_VLOG_POSTED.name
                )
                .withSubtype(V1.VlogGainedLikesPayload::class.java, ActionType.VLOG_GAINED_LIKES.name)
                .withSubtype(V1.VlogNewReactionPayload::class.java, ActionType.VLOG_NEW_REACTION.name)
                .withSubtype(V1.VlogRecordRequestPayload::class.java, ActionType.VLOG_RECORD_REQUEST.name)
        )
        .add(KotlinJsonAdapterFactory())
        .build()

    fun parse(data: Map<String, *>): BaseNotification? {
        val adapter: JsonAdapter<BaseNotification> = moshi.adapter(BaseNotification::class.java)

        // Return the Notification object, created from notification payload
        return adapter.fromJson(data.toString())
    }

    fun getIntent(context: Context, notification: BaseNotification?): Intent? {
        var intent: Intent? = Intent(context, AppActivity::class.java)
        // Retrieve action from notification payload or null if none exists
        val action = notification?.clickAction

        // Assign correct action if notification contains payload
        action?.let {
            try {
                intent = when (action) {
                    ActionType.VLOG_RECORD_REQUEST -> null
                    ActionType.VLOG_NEW_REACTION -> null
                    else -> null
                }
            } catch (e: IllegalArgumentException) {
                e.message?.let {
                    Log.e(TAG, it)
                }
            }
        }

        return intent
    }

    companion object {
        private const val TAG = "NotificationHandler"
    }
}
