@file:Suppress("IllegalIdentifier")

package com.laixer.sample.presentation.model

import com.laixer.sample.domain.usecase.CombinedUserVlog
import com.laixer.sample.vlog
import com.laixer.sample.user
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
        assertTrue(vlogItem.firstName == user.firstName)
        assertTrue(vlogItem.lastName == user.lastName)
        assertTrue(vlogItem.nickname == user.nickname)
        assertTrue(vlogItem.email == user.email)
    }
}
