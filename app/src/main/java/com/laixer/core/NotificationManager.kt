package com.laixer.core

import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import org.json.JSONObject

data class Notification (
    @field:Json(name = "protocol") val protocol: String,
    @field:Json(name = "protocol_version") val version: Int,
    @field:Json(name = "data_type") val data_type: String,
    @field:Json(name = "data_type_version") val data_type_version: Int,
    @field:Json(name = "data") private val dataString: NotificationPayload,
    @field:Json(name = "content_type") val content_type: String,
    @field:Json(name = "timestamp") val timestamp: String,
    @field:Json(name = "user_agent") val user_agent: String
    //var payload: NotificationPayload
)

data class NotificationPayload(
    @field:Json(name = "notification_type") val notification_type: String,
    @field:Json(name = "title") val title: String,
    @field:Json(name = "message") val message: String,
    @field:Json(name = "click_action") val click_action: String,
    @field:Json(name = "id") val id: String
)

class PayloadAdapter: JsonAdapter<NotificationPayload>() {
    override fun fromJson(reader: JsonReader): NotificationPayload? {
        val moshi = Moshi.Builder().build()
        val jsonAdapter = moshi.adapter<NotificationPayload>(NotificationPayload::class.java!!)

        return jsonAdapter.fromJson(reader.nextString())
    }

    override fun toJson(writer: JsonWriter, value: NotificationPayload?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

//class vlogPayload(map: AbstractMap<*, *>): NotificationPayload {
//    @field:Json(name = "id") val id: String
//}

class NotificationManager {
    val payloadAdapter = PayloadAdapter()

    val moshi: Moshi = Moshi.Builder().add(payloadAdapter).build()
    fun handleNotification(data: Map<String, String>): Notification? {
        val adapter: JsonAdapter<Notification> = moshi.adapter(Notification::class.java)

        // Creates JSONObject from notification payload
        val jsonData = JSONObject(data).toString()
        val notification = adapter.fromJson(jsonData)

        // Uses reflection to find the appropriate data class
        // val clazz = Class.forName("com.laixer.core.${notification?.data_type}").kotlin

        return notification
    }
}