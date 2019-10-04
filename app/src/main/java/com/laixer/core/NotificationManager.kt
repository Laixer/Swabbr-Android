package com.laixer.core

import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import org.json.JSONObject
import java.util.AbstractMap

data class notification (
    @field:Json(name = "protocol") val protocol: String,
    @field:Json(name = "version") val version: Int,
    @field:Json(name = "data_type") val data_type: String,
    @field:Json(name = "data") val data: String,
    @field:Json(name = "content_type") val content_type: String,
    @field:Json(name = "timestamp") val timestamp: String,
    @field:Json(name = "user_agent") val user_agent: String,
    @field:Json(name = "notification_type") val notification_type: String
)

abstract class Data(map: AbstractMap<*, *>)

class vlog1(map: AbstractMap<*, *>): Data(map) {
    val title: String = map["title"] as String
    val message: String = map["message"] as String
    val id: String = map["id"] as String
}

class NotificationManager {
    val moshi: Moshi = Moshi.Builder().build()

    fun handleNotification(data: Map<String, String>): notification? {
        val adapter: JsonAdapter<notification> = moshi.adapter(notification::class.java)

        // Creates JSONObject from notification payload
        val jsonData = JSONObject(data).toString()
        val notification = adapter.fromJson(jsonData)

        // Uses reflection to find the appropriate data class
        // val clazz = Class.forName("com.laixer.core.${notification?.data_type}").kotlin

        return notification
    }

    fun handleNotificationData(notification: notification): vlog1 {
        // Create JSONObject from data within notification payload
        val jsonDataVlog = JSONObject(notification?.data).toString()
        val adapterVlog = moshi.adapter<Any>(Object::class.java) // returns AbstractMap

        // Create data (i.e. notification title and body) Abstract map
        return vlog1(adapterVlog.fromJson(jsonDataVlog) as AbstractMap<*, *>)
    }
}