package com.laixer.swabbr.domain.model

import com.laixer.swabbr.Entities
import com.laixer.swabbr.Models
import com.laixer.swabbr.data.datasource.model.mapToData
import org.junit.Assert.assertEquals
import org.junit.Test

class VlogTest {

    val entity = Entities.vlog
    val model = Models.vlog

    @Test
    fun `map entity to domain`() {
        // given
        // when
        val transformedModel = model.mapToData()
        // then
        assertEquals(entity, transformedModel)
    }
}
