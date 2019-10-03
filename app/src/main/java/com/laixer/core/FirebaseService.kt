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
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.laixer.navigation.features.CameraNavigation
import com.laixer.navigation.features.SampleNavigation
import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import org.json.JSONObject
import java.lang.UnsupportedOperationException
import java.util.*
import kotlin.reflect.KClass

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

class Vlog1(map: AbstractMap<*, *>): Data(map) {
    val title: String = map["title"] as String
    val message: String = map["message"] as String
    val id: String = map["id"] as String
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

        val moshi: Moshi = Moshi.Builder().build()
        val adapter: JsonAdapter<notification> = moshi.adapter(notification::class.java)

        Log.d(TAG, "From: ${remoteMessage.from}")

        // Check if message contains a notification payload.
        remoteMessage.data.let {

            // Creates JSONObject from notification payload
            val jsonData = JSONObject(remoteMessage.data).toString()
            val notification = adapter.fromJson(jsonData)

            // Uses reflection to find the appropriate data class
            val clazz = Class.forName("com.laixer.core.${notification?.data_type}").kotlin

            // Create JSONObject from data within notification payload
            val jsonDataVlog = JSONObject(notification?.data).toString()
            val adapterVlog = moshi.adapter<Any>(Object::class.java) // returns AbstractMap

            //
            val data = Vlog1(adapterVlog.fromJson(jsonDataVlog) as AbstractMap<*, *>)

        sendNotification(notification, data)
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
    private fun scheduleJob(remoteMessage: RemoteMessage) {
        // [START dispatch_job]
        val work = OneTimeWorkRequest.Builder(MyWorker::class.java).build()
        WorkManager.getInstance(this.baseContext).beginWith(work).enqueue()
        // [END dispatch_job]

        if (!remoteMessage.notification?.clickAction.isNullOrEmpty()) {

        }
    }

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
    private fun sendNotification(notification: notification?, data: Vlog1) {
        var intent: Intent = when (notification?.notification_type) {
            "vlog_record_request" -> CameraNavigation.dynamicStart!!
            "followed_profile_live" -> SampleNavigation.vlogDetails("1", "101")!!
            else -> Intent(this, MainActivity::class.java)
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )
        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_stat_ic_notification)
            .setContentTitle(data.title)
            .setContentText(data.message)
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
