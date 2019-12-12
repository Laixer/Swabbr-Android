@file:Suppress("IllegalIdentifier")

package com.laixer.swabbr.presentation.model

import com.laixer.swabbr.domain.usecase.CombinedUserVlog
import com.laixer.swabbr.vlog
import com.laixer.swabbr.user
import org.junit.Assert.assertTrue
import org.junit.Test

class VlogItemTest {

    @Test
    fun `map domain to presentation`() {
        // given
        val combinedUserVlog = CombinedUserVlog(user, vlog)

        // when
        val vlogItem = combinedUserVlog.mapToPresentation()

        // then
        assertTrue(vlogItem.vlogId == vlog.id)
        assertTrue(vlogItem.userId == user.id)
        assertTrue(vlogItem.duration == vlog.duration)
        assertTrue(vlogItem.startDate == vlog.startDate)
        assertTrue(vlogItem.isLive == vlog.isLive)
        assertTrue(vlogItem.totalViews == vlog.totalViews)
        assertTrue(vlogItem.totalReactions == vlog.totalReactions)
        assertTrue(vlogItem.totalLikes == vlog.totalLikes)
    }
}