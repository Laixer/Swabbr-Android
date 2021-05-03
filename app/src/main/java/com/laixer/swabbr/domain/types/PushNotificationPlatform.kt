package com.laixer.swabbr.domain.types

/**
 * Enum representing a push notification platform.
 */
enum class PushNotificationPlatform(val value: Int) {
    APNS(0),
    FCM(1)
}
