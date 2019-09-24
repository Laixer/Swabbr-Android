@file:Suppress("IllegalIdentifier")

package com.laixer.sample.datasource.model

import com.laixer.sample.vlogEntity
import org.junit.Assert.assertTrue
import org.junit.Test

class VlogEntityTest {

    @Test
    fun `map entity to domain`() {
        // given

        // when
        val model = vlogEntity.mapToDomain()

        // then
        assertTrue(model.userId == vlogEntity.userId)
        assertTrue(model.id == vlogEntity.id)
        assertTrue(model.duration == vlogEntity.duration)
        assertTrue(model.startDate == vlogEntity.startDate)
        assertTrue(model.totalViews == vlogEntity.totalViews)
        assertTrue(model.totalReactions == vlogEntity.totalReactions)
        assertTrue(model.totalLikes == vlogEntity.totalLikes)
        assertTrue(model.isLive == vlogEntity.isLive)
        assertTrue(model.isPrivate == vlogEntity.isPrivate)

    }
}
