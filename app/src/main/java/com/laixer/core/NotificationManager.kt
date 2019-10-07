package com.laixer.core

import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import org.json.JSONObject
import java.util.AbstractMap

class Notification (
    @field:Json(name = "protocol") val protocol: String,
    @field:Json(name = "protocol_version") val version: Int,
    @field:Json(name = "data_type") val data_type: String,
    @field:Json(name = "data_type_version") val data_type_version: Int,
    @field:Json(name = "data") private val dataString: String,
    @field:Json(name = "content_type") val content_type: String,
    @field:Json(name = "timestamp") val timestamp: String,
    @field:Json(name = "user_agent") val user_agent: String,
    var payload: NotificationPayload
) {
    fun setDataObject(){
        val moshi: Moshi = Moshi.Builder().build()

        // Create JSONObject from data within notification payload
        val jsonDataVlog = JSONObject(dataString).toString()
        val adapterVlog = moshi.adapter<Any>(Object::class.java) // returns AbstractMap

        // Create data (i.e. notification title and body) Abstract map
        payload = NotificationPayload(adapterVlog.fromJson(jsonDataVlog) as AbstractMap<*, *>)
    }
}

abstract class Data(map: AbstractMap<*, *>)

class NotificationPayload(map: AbstractMap<*, *>): Data(map) {
    val title: String = map["title"] as String
    val message: String = map["message"] as String
    val click_action: String = map["click_action"] as String
    val id: String = map["id"] as String
}

//class vlogPayload(map: AbstractMap<*, *>): NotificationPayload {
//    val a = "s"
//}

class NotificationManager {
    val moshi: Moshi = Moshi.Builder().build()

    fun handleNotification(data: Map<String, String>): Notification? {
        val adapter: JsonAdapter<Notification> = moshi.adapter(Notification::class.java)

        // Creates JSONObject from notification payload
        val jsonData = JSONObject(data).toString()
        val notification = adapter.fromJson(jsonData)

        // Uses reflection to find the appropriate data class
        // val clazz = Class.forName("com.laixer.core.${notification?.data_type}").kotlin
        notification?.setDataObject()

        return notification
    }
}