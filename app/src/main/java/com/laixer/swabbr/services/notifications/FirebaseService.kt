package com.laixer.swabbr.services.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.laixer.swabbr.R
import com.laixer.swabbr.services.users.UserService
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.util.*

// TODO Use abstraction for user service
class FirebaseService : FirebaseMessagingService(), KoinComponent {

    private val userService: UserService by inject()
    private val notificationHandler by lazy { NotificationHandler() }

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d(TAG, "From: ${remoteMessage.from}")
        remoteMessage.data.values.first()?.let {
            try {
                sendNotification(notificationHandler.parse(it))

                // If possible, refresh the token
                userService.hasValidToken() // TODO Ugly
            } catch (e: Exception) {
                Log.e(TAG, e.message!!)
            }
        }
    }

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed Firebase token: $token")
        // Manage the Firebase subscription on the server side
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private fun sendRegistrationToServer(token: String?) {
        throw UnsupportedOperationException(token)
    }

    /**
     * Create and show a simple notification containing
     * the received FCM message. This will generate the
     * actual notification popup on the phone.
     *
     * @param notification FCM message body received.
     */
    private fun sendNotification(notification: V1.Notification?) {
        // Set default intent
        val pendingIntent = notificationHandler.getPendingIntent(baseContext, notification)
        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val title = notification?.data?.title?.let(this@FirebaseService::getString)
            ?: getString(R.string.default_notification_title)

        val message = notification?.data?.message?.let(this@FirebaseService::getString)
            ?: getString(R.string.default_notification_message)

        // TODO How to catch invalid intents?
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_logo_icon)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .setTimeoutAfter(NOTIFICATION_TIMEOUT_IN_MILLIS)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        // This will give each notification a unique id to prevent overwriting them.
        notificationManager.notify(
            Random().nextInt(),
            notificationBuilder.build()
        )
    }

    companion object {
        private val TAG = FirebaseService::class.java.simpleName
        private const val NOTIFICATION_CHANNEL_ID = "nh-demo-channel-id"
        private const val NOTIFICATION_CHANNEL_NAME = "Notification Hubs Demo Channel"
        private const val NOTIFICATION_CHANNEL_DESCRIPTION = "Notification Hubs Demo Channel"
        private const val NOTIFICATION_TIMEOUT_IN_MILLIS = 1_800_000L // 30 minutes (30 * 60 * 1000 ms)

        fun createChannelAndHandleNotifications(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    NOTIFICATION_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                )
                channel.description =
                    NOTIFICATION_CHANNEL_DESCRIPTION
                channel.setShowBadge(true)

                context.getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
            }
        }
    }
}
