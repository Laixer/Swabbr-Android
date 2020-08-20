package com.laixer.swabbr.data.datasource.model

import com.laixer.swabbr.Entities
import com.laixer.swabbr.Models
import org.junit.Assert.assertEquals
import org.junit.Test

class ReactionEntityTest {

    val entity = Entities.reaction
    val model = Models.reaction

    @Test
    fun `map entity to domain`() {
        // given
        // when
        val transformedEntity = entity.mapToDomain()
        // then
        assertEquals(model, transformedEntity)
    }
}
