@file:Suppress("IllegalIdentifier")

package com.laixer.core

import org.junit.Assert.assertEquals
import org.junit.Test

class NotificationManagerTest {
    private val mockData = mapOf(
        "protocol" to "swabbr",
        "protocol_version" to 1,
        "data_type" to "notification",
        "data_type_version" to 1,
        "data" to "{\"id\":\"101\",\"title\":\"Reactie\",\"message\":\"Nieuwe reactie op vlog\",\"click_action\":\"vlog_new_reaction\"}",
        "content_type" to "json",
        "timestamp" to "2019-10-01",
        "user_agent" to "<user_agent>"
    )

    private val notificationManager = NotificationManager()

    @Test
    fun `create notification object from message`() {
        // given

        // when
        val handledData = notificationManager.handleNotification(mockData)

        // then
        assertEquals(handledData?.protocol, mockData["protocol"])
        assertEquals(handledData?.protocolVersion, mockData["protocol_version"])
        assertEquals(handledData?.dataType, mockData["data_type"])
        assertEquals(handledData?.dataTypeVersion, mockData["data_type_version"])
        assertEquals(handledData?.contentType, mockData["content_type"])
        assertEquals(handledData?.timestamp, mockData["timestamp"])
        assertEquals(handledData?.userAgent, mockData["user_agent"])
        // check inner data payload
        assertEquals(handledData?.data?.title, "Reactie")
        assertEquals(handledData?.data?.message, "Nieuwe reactie op vlog")
        assertEquals(handledData?.data?.clickAction, "vlog_new_reaction")
        assertEquals(handledData?.data?.id, "101")
    }
}
