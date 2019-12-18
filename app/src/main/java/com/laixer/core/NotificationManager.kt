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

open class NotificationPayload(
    @field:Json(name = "title") val title: String? = null,
    @field:Json(name = "message") val message: String? = null,
    @field:Json(name = "click_action") val clickAction: String? = null,
    @field:Json(name = "cloud_code") val cloudCode: String?,
    @field:Json(name = "host_address") val hostAddress: String,
    @field:Json(name = "app_name") val appName: String,
    @field:Json(name = "stream_name") val streamName: String,
    @field:Json(name = "port") val port: Int
)

class NotificationManager {
    private val moshi: Moshi = Moshi.Builder().build()

    fun handleNotification(data: Map<String, *>): Notification? {
        val adapter: JsonAdapter<Notification> = moshi.adapter(Notification::class.java).lenient()

        // Return the Notification object, created from notification payload
        return adapter.fromJson(data.toString())
    }
}
