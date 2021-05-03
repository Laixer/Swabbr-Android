package com.laixer.swabbr.services.notifications

import android.app.PendingIntent
import android.content.Context
import android.util.Log
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

// TODO Why are all notifications nullable? Doesn't seem right.
/**
 *  Handles incoming notifications. Note that this also
 *  manages notification parsing through [Moshi] using
 *  [NotificationJsonAdapterFactory].
 */
class NotificationHandler {
    private val moshi: Moshi = Moshi.Builder()
        .add(NotificationJsonAdapterFactory())
        .add(KotlinJsonAdapterFactory())
        .build()

    /**
     *  Parse a notification json string into a notification object.
     *
     *  @param notificationJson The complete notification as string.
     */
    fun parse(notificationJson: String): V1.Notification? = moshi.adapter(
        V1.Notification::class.java
    ).fromJson(notificationJson)

    /**
     *  Attempts to get the intent attached to a notification.
     *  This represents the fragment or activity where the
     *  notification should take us when clicked.
     *
     *  @param context Context.
     *  @param notification Notification object.
     */
    fun getPendingIntent(context: Context, notification: V1.Notification?): PendingIntent? {
        // Assign correct action if notification contains payload
        return try {
            notification?.data?.createPendingIntent(context)
                ?: throw IllegalArgumentException(
                    "Unable to create a pending intent for notification" +
                        " ${notification?.notificationAction} - ${notification?.notificationActionString}"
                )
        } catch (e: IllegalArgumentException) {
            e.message?.let { Log.e(TAG, it) }
            null
        }
    }

    companion object {
        private const val TAG = "NotificationHandler"
    }
}
