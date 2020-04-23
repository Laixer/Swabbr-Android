package com.laixer.core

import android.content.Context
import android.content.Intent
import android.util.Log
import com.laixer.navigation.features.SwabbrNavigation
import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import java.util.Locale

enum class Action {
    VLOG_NEW_REACTION, VLOG_RECORD_REQUEST
}

data class SwabbrNotification(
    @field:Json(name = "protocol") val protocol: String,
    @field:Json(name = "protocol_version") val protocolVersion: Int,
    @field:Json(name = "data_type") val dataType: String,
    @field:Json(name = "data_type_version") val dataTypeVersion: Int,
    @field:Json(name = "data") val data: SwabbrNotificationPayload,
    @field:Json(name = "content_type") val contentType: String,
    @field:Json(name = "timestamp") val timestamp: String,
    @field:Json(name = "user_agent") val userAgent: String
)

open class SwabbrNotificationPayload(
    @field:Json(name = "title") val title: String,
    @field:Json(name = "message") val message: String? = null,
    @field:Json(name = "click_action") val clickAction: String? = null,
    @field:Json(name = "cloud_code") val cloudCode: String?,
    @field:Json(name = "host_address") val hostAddress: String,
    @field:Json(name = "app_name") val appName: String,
    @field:Json(name = "stream_name") val streamName: String,
    @field:Json(name = "port") val port: Int
)

class NotificationHandler {
    private val moshi: Moshi = Moshi.Builder().build()

    fun parse(data: Map<String, *>): SwabbrNotification? {
        val adapter: JsonAdapter<SwabbrNotification> = moshi.adapter(
            SwabbrNotification::class.java
        ).lenient()

        // Return the Notification object, created from notification payload
        return adapter.fromJson(data.toString())
    }

    fun getIntent(context: Context, notification: SwabbrNotification?): Intent? {
        var intent = Intent(context, MainActivity::class.java)
        // Retrieve action from notification payload or null if none exists
        val action = notification?.data?.clickAction?.toUpperCase(Locale.ROOT)

        // Assign correct action if notification contains payload
        action?.let {
            try {
                intent = when (Action.valueOf(action)) {
                    Action.VLOG_RECORD_REQUEST -> SwabbrNavigation.record(
                        SwabbrNavigation.ConnectionSettings(
                            notification.data.cloudCode,
                            notification.data.hostAddress,
                            notification.data.appName,
                            notification.data.streamName,
                            notification.data.port
                        )
                    )!!
                    Action.VLOG_NEW_REACTION -> SwabbrNavigation.vlogDetails(
                        arrayListOf(
                            notification.data.port.toString()
                        )
                    )!!
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
