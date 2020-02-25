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
        assertEquals(model.startDate, vlogEntity.startDate)
        assertEquals(model.isLive, vlogEntity.isLive)
        assertEquals(model.isPrivate, vlogEntity.isPrivate)
    }
}
