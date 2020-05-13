package com.laixer.swabbr.services.notifications

import com.laixer.swabbr.Notifications
import com.laixer.swabbr.services.notifications.protocols.BaseNotification
import com.laixer.swabbr.services.notifications.protocols.V1
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.reactivex.Single
import org.junit.Before
import org.junit.Test

class NotificationHandlerTest {
    lateinit var moshi: Moshi
    lateinit var adapter: JsonAdapter<BaseNotification>

    private val baseNotification = Notifications.baseNotification
    private val followed_profile_live_notification = Notifications.followed_profile_live

    @Before
    fun setUp() {
        moshi = Moshi.Builder()
            .add(
                PolymorphicJsonAdapterFactory.of(V1.BaseNotificationPayload::class.java, "clickAction")
                    .withSubtype(V1.FollowedProfileLivePayload::class.java, ActionType.FOLLOWED_PROFILE_LIVE.name)
                    .withSubtype(
                        V1.FollowedProfileVlogPostedPayload::class.java,
                        ActionType.FOLLOWED_PROFILE_VLOG_POSTED.name
                    )
                    .withSubtype(V1.VlogGainedLikesPayload::class.java, ActionType.VLOG_GAINED_LIKES.name)
                    .withSubtype(V1.VlogNewReactionPayload::class.java, ActionType.VLOG_NEW_REACTION.name)
                    .withSubtype(V1.VlogRecordRequestPayload::class.java, ActionType.VLOG_RECORD_REQUEST.name)
            )
            .add(KotlinJsonAdapterFactory())
            .build()

        adapter = moshi.adapter(BaseNotification::class.java)

    }

    @Test
    fun `test parsing "followed profile live" notification success`() {
        val item = adapter.fromJson(adapter.toJson(followed_profile_live_notification))

        assert(item?.clickAction == ActionType.FOLLOWED_PROFILE_LIVE)
    }
}
