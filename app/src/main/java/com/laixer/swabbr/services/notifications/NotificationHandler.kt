package com.laixer.swabbr.services.notifications

import android.app.PendingIntent
import android.content.Context
import android.util.Log
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class NotificationHandler {
    private val moshi: Moshi = Moshi.Builder()
        .add(NotificationFactory())
        .add(KotlinJsonAdapterFactory())
        .build()

    // Return the Notification object, created from notification payload
    fun parse(data: String): V1.Notification? = moshi.adapter(
        V1.Notification::class.java).fromJson(data)

    fun getPendingIntent(context: Context, notification: V1.Notification?): PendingIntent? {
        // Retrieve action from notification payload or null if none exists
        val action = notification?.clickAction
        // Assign correct action if notification contains payload
        return try {
            notification?.data?.createPendingIntent(context)
                ?: throw IllegalArgumentException("Unable to create a pending intent for notification ${notification?.clickAction}")
        } catch (e: IllegalArgumentException) {
            e.message?.let { Log.e(TAG, it) }
            null
        }
    }

    companion object {
        private const val TAG = "NotificationHandler"
    }
}
