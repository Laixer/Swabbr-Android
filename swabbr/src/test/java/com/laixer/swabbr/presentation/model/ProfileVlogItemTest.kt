@file:Suppress("IllegalIdentifier")

package com.laixer.swabbr.presentation.model

import com.laixer.swabbr.user
import com.laixer.swabbr.vlog
import org.junit.Assert.assertEquals
import org.junit.Test

class ProfileVlogItemTest {

    @Test
    fun `map domain to presentation`() {
        // given
        val pairUserVlog = Pair(user, vlog)

        // when
        val profileVlogItem = pairUserVlog.mapToPresentation()

        // then
        assertEquals(profileVlogItem.userId, user.id)
        assertEquals(profileVlogItem.nickname, user.nickname)
        assertEquals(profileVlogItem.firstName, user.firstName)
        assertEquals(profileVlogItem.lastName, user.lastName)
        assertEquals(profileVlogItem.vlogId, vlog.id)
        assertEquals(profileVlogItem.duration, vlog.duration)
        assertEquals(profileVlogItem.startDate, vlog.startDate)
        assertEquals(profileVlogItem.isLive, vlog.isLive)
        assertEquals(profileVlogItem.totalViews, vlog.totalViews)
        assertEquals(profileVlogItem.totalReactions, vlog.totalReactions)
        assertEquals(profileVlogItem.totalLikes, vlog.totalLikes)
    }
}
