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
        assertTrue(vlogItem.vlogId == vlog.vlogId)
        assertTrue(vlogItem.userId == user.id)
        assertTrue(vlogItem.startDate == vlog.startDate)
        assertTrue(vlogItem.isLive == vlog.isLive)
    }
}
