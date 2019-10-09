package com.laixer.core

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.laixer.navigation.features.CameraNavigation
import com.laixer.navigation.features.SampleNavigation
import java.lang.UnsupportedOperationException
import java.util.*
import kotlin.IllegalArgumentException

enum class Action {
    VLOG_NEW_REACTION,
    VLOG_RECORD_REQUEST
}

class FirebaseService : FirebaseMessagingService() {

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d(TAG, "From: ${remoteMessage.from}")

        // Check if message contains a notification payload.
        remoteMessage.data.let {
            val notificationManager = NotificationManager()
            val notification = notificationManager.handleNotification(remoteMessage.data)

            sendNotification(notification)
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

    // [START on_new_token]
    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        // sendRegistrationToServer(token)
    }
    // [END on_new_token]

    /**
     * Schedule async work using WorkManager.
     */
//    private fun scheduleJob(remoteMessage: RemoteMessage) {
//        // [START dispatch_job]
//        val work = OneTimeWorkRequest.Builder(MyWorker::class.java).build()
//        WorkManager.getInstance(this.baseContext).beginWith(work).enqueue()
//        // [END dispatch_job]
//
//        if (!remoteMessage.notification?.clickAction.isNullOrEmpty()) {
//
//        }
//    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private fun handleNow() {
        Log.d(TAG, "Short lived task is done.")
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
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private fun sendNotification(notification: Notification?) {
        // Set default intent
        var intent = Intent(this, MainActivity::class.java)

        // Retrieve action from notification payload or null if none exists
        val action = notification!!.data.clickAction.toUpperCase(Locale.ROOT)

        // Assign correct action if notification contains payload
        action.let {
            try {
                intent = when (Action.valueOf(action)) {
                    Action.VLOG_RECORD_REQUEST -> CameraNavigation.dynamicStart!!
                    Action.VLOG_NEW_REACTION -> SampleNavigation.vlogDetails(notification.data.id)!!
                }
            } catch (e: IllegalArgumentException) {
                Log.e(TAG, e.message)
            }
        }

        // Clear activity stack
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        // Blabla
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )
        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_stat_ic_notification)
            .setContentTitle(notification.data.title)
            .setContentText(notification.data.message)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

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

        notificationManager.notify(
            NOTIFICATION_ID /* ID of notification */,
            notificationBuilder.build()
        )
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
        private const val NOTIFICATION_CHANNEL_ID = "nh-demo-channel-id"
        private const val NOTIFICATION_CHANNEL_NAME = "Notification Hubs Demo Channel"
        private const val NOTIFICATION_CHANNEL_DESCRIPTION = "Notification Hubs Demo Channel"
        private const val NOTIFICATION_ID = 1

        fun createChannelAndHandleNotifications(context: Context) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    NOTIFICATION_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                )
                channel.description = NOTIFICATION_CHANNEL_DESCRIPTION
                channel.setShowBadge(true)

                val notificationManager = context.getSystemService(NotificationManager::class.java)
                notificationManager.createNotificationChannel(channel)
            }
        }
    }
}
