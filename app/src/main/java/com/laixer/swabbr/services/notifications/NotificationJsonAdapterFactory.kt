package com.laixer.swabbr.services.notifications

import com.squareup.moshi.*
import java.lang.reflect.Type
import java.time.ZonedDateTime

/**
 *  Factory that creates [V1.Notification] objects from json.
 */
class NotificationJsonAdapterFactory : JsonAdapter.Factory {
    /**
     *  Parse JSON using [Moshi].
     */
    override fun create(type: Type, annotations: MutableSet<out Annotation>, moshi: Moshi): JsonAdapter<*>? {
        if (!Types.getRawType(type).isAssignableFrom(V1.Notification::class.java)) {
            return null
        }

        /**
         *  Object containing parsing functionality.
         */
        return object : JsonAdapter<V1.Notification>() {
            /**
             *  Parse a [JsonReader] to a [V1.Notification] object.
             *
             *  @param reader The reader to parse from.
             */
            override fun fromJson(reader: JsonReader): V1.Notification {
                val jsonValue = reader.readJsonValue()


                // First explicitly extract the data object.
                @Suppress("UNCHECKED_CAST")
                val valueMap = jsonValue as Map<String, Any>
                val normalizedValueMap = valueMap.entries.associate { it.key.toLowerCase() to it.value }

                val notificationAction = NotificationAction.values()[(normalizedValueMap["notificationaction"] as Double).toInt()]
                val normalizedRawData = (normalizedValueMap["data"] as Map<String, Any>).entries.associate { it.key.toLowerCase() to it.value }
                val notificationData = moshi.adapter(notificationAction.getDerivedDataClass())
                    .fromJsonValue(normalizedRawData)
                    ?: throw JsonDataException()

                // Then build the json object, attaching the extracted data object.
                return V1.Notification(
                    protocol = normalizedValueMap["protocol"] as String,
                    protocolVersion = normalizedValueMap["protocolversion"] as String,
                    notificationAction = notificationAction,
                    notificationActionString = normalizedValueMap["notificationactionstring"] as String,
                    timestamp = ZonedDateTime.parse(normalizedValueMap["timestamp"] as String),
                    userAgent = normalizedValueMap["useragent"] as String,
                    data = notificationData
                )
            }

            override fun toJson(writer: JsonWriter, value: V1.Notification?) {
                TODO("Not yet implemented")
            }

            /**
             *  Extract the data class from the [NotificationAction].
             */
            fun NotificationAction.getDerivedDataClass(): Class<out V1.NotificationData> = when (this) {
                NotificationAction.FOLLOWED_PROFILE_VLOG_POSTED -> V1.NotificationData.FollowedProfileVlogPosted::class.java
                NotificationAction.VLOG_GAINED_LIKE -> V1.NotificationData.VlogGainedLikes::class.java
                NotificationAction.VLOG_NEW_REACTION -> V1.NotificationData.VlogNewReaction::class.java
                NotificationAction.VLOG_RECORD_REQUEST -> V1.NotificationData.VlogRecordRequest::class.java
            }
        }
    }
}
