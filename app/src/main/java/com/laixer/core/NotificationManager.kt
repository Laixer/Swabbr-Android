package com.laixer.core

import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi

data class Notification (
    @field:Json(name = "protocol") val protocol: String,
    @field:Json(name = "protocol_version") val version: Int,
    @field:Json(name = "data_type") val data_type: String,
    @field:Json(name = "data_type_version") val data_type_version: Int,
    @field:Json(name = "data1") val data: NotificationPayload,
    @field:Json(name = "content_type") val content_type: String,
    @field:Json(name = "timestamp") val timestamp: String,
    @field:Json(name = "user_agent") val user_agent: String
)

data class NotificationPayload(
    @field:Json(name = "notification_type") val notification_type: String,
    @field:Json(name = "title") val title: String,
    @field:Json(name = "message") val message: String,
    @field:Json(name = "click_action") val click_action: String,
    @field:Json(name = "id") val id: String
)

//class vlogPayload(map: AbstractMap<*, *>): NotificationPayload {
//    @field:Json(name = "id") val id: String
//}

class NotificationManager {
    val moshi: Moshi = Moshi.Builder().build()

    fun handleNotification(data: Map<String, *>): Notification? {
        val adapter: JsonAdapter<Notification> = moshi.adapter(Notification::class.java).lenient()

        // Return the Notification object, created from notification payload
        return adapter.fromJson(data.toString())

        // Uses reflection to find the appropriate data class
        // val clazz = Class.forName("com.laixer.core.${notification?.data_type}").kotlin
    }
}