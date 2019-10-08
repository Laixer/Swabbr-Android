package com.laixer.core

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi

data class Notification (
    val protocol: String,
    val protocol_version: Int,
    val data_type: String,
    val data_type_version: Int,
    val data: NotificationPayload,
    val content_type: String,
    val timestamp: String,
    val user_agent: String
)

data class NotificationPayload(
    val notification_type: String,
    val title: String,
    val message: String,
    val click_action: String,
    val id: String
)

class NotificationManager {
    val moshi: Moshi = Moshi.Builder().build()

    fun handleNotification(data: Map<String, *>): Notification? {
        val adapter: JsonAdapter<Notification> = moshi.adapter(Notification::class.java).lenient()

        // Return the Notification object, created from notification payload
        return adapter.fromJson(data.toString())
    }
}