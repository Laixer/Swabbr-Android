package com.laixer.swabbr.services.notifications.protocols

import com.laixer.swabbr.services.notifications.ActionType
import com.squareup.moshi.Json

data class BaseNotification(
    @field:Json(name = "protocol") val protocol: String,
    @field:Json(name = "protocol_version") val protocolVersion: String,
    @field:Json(name = "data_type") val dataType: String,
    @field:Json(name = "data_type_version") val dataTypeVersion: String,
    @field:Json(name = "clickAction") var clickAction: ActionType?,
    @field:Json(name = "content_type") val contentType: String,
    @field:Json(name = "timestamp") val timestamp: String,
    @field:Json(name = "user_agent") val userAgent: String,
    @field:Json(name = "data") var data: V1.BaseNotificationPayload?
)
