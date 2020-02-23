@file:Suppress("IllegalIdentifier")

package com.laixer.swabbr.datasource.model

import com.laixer.swabbr.vlogEntity
import org.junit.Assert.assertEquals
import org.junit.Test

class VlogEntityTest {

    @Test
    fun `map entity to domain`() {
        // given
        // when
        val model = vlogEntity.mapToDomain()
        // then
        assertEquals(model.userId, vlogEntity.userId)
        assertEquals(model.id, vlogEntity.id)
        assertEquals(model.duration, vlogEntity.duration)
        assertEquals(model.startDate, vlogEntity.startDate)
        assertEquals(model.totalViews, vlogEntity.totalViews)
        assertEquals(model.totalReactions, vlogEntity.totalReactions)
        assertEquals(model.totalLikes, vlogEntity.totalLikes)
        assertEquals(model.isLive, vlogEntity.isLive)
        assertEquals(model.isPrivate, vlogEntity.isPrivate)
    }
}
