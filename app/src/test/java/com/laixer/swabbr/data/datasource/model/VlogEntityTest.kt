package com.laixer.swabbr.data.datasource.model

import com.laixer.swabbr.Entities
import com.laixer.swabbr.Models
import org.junit.Assert.assertEquals
import org.junit.Test

class VlogEntityTest {

    val entity = Entities.vlog
    val model = Models.vlog

    @Test
    fun `map entity to domain`() {
        // given
        // when
        val transformedEntity = entity.mapToDomain()
        // then
        assertEquals(model, transformedEntity)
    }
}
