package com.laixer.core

import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi

data class Notification(
    @field:Json(name = "protocol") val protocol: String,
    @field:Json(name = "protocol_version") val protocolVersion: Int,
    @field:Json(name = "data_type") val dataType: String,
    @field:Json(name = "data_type_version") val dataTypeVersion: Int,
    @field:Json(name = "data") val data: NotificationPayload,
    @field:Json(name = "content_type") val contentType: String,
    @field:Json(name = "timestamp") val timestamp: String,
    @field:Json(name = "user_agent") val userAgent: String
)

data class NotificationPayload(
    @field:Json(name = "title") val title: String,
    @field:Json(name = "message") val message: String,
    @field:Json(name = "click_action") val clickAction: String,
    @field:Json(name = "id") val id: String
)

class NotificationManager {
    private val moshi: Moshi = Moshi.Builder().build()

    fun handleNotification(data: Map<String, *>): Notification? {
        val adapter: JsonAdapter<Notification> = moshi.adapter(Notification::class.java).lenient()

        // Return the Notification object, created from notification payload
        return adapter.fromJson(data.toString())
    }
}
