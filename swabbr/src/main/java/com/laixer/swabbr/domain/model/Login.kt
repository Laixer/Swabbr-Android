package com.laixer.swabbr.domain.model

data class Login(
    val username: String,
    val password: String,
    val remember: Boolean,
    val pushNotificationPlatform: PushNotificationPlatform,
    val handle: String
)

enum class PushNotificationPlatform(val value: String) {
    APNS("apns"),
    FCM("fcm")
}
