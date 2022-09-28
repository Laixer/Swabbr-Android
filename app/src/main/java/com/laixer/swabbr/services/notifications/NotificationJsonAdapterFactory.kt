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
                val value = jsonValue as Map<String, Any>
                val notificationAction = NotificationAction.values()[(value["notificationAction"] as Double).toInt()]
                val data = moshi.adapter(notificationAction.getDerivedDataClass())
                    .fromJsonValue(value["data"])
                    ?: throw JsonDataException()

                // Then build the json object, attaching the extracted data object.
                return V1.Notification(
                    protocol = value["protocol"] as String,
                    protocolVersion = value["protocolVersion"] as String,
                    notificationAction = notificationAction,
                    notificationActionString = value["notificationActionString"] as String,
                    timestamp = ZonedDateTime.parse(value["timestamp"] as String),
                    userAgent = value["userAgent"] as String,
                    data = data
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
