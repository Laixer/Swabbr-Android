package com.laixer.swabbr.domain.model

import com.laixer.swabbr.Entities
import com.laixer.swabbr.Models
import com.laixer.swabbr.data.model.mapToData
import org.junit.Assert.assertEquals
import org.junit.Test

class ReactionTest {

    val entity = Entities.reaction
    val model = Models.reaction

    @Test
    fun `map entity to domain`() {
        // given
        // when
        val transformedModel = model.mapToData()
        // then
        assertEquals(entity, transformedModel)
    }
}
