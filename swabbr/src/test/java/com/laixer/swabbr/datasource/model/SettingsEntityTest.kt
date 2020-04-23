package com.laixer.swabbr.datasource.model

import com.laixer.swabbr.Entities
import com.laixer.swabbr.Models
import org.junit.Assert.assertEquals
import org.junit.Test

class SettingsEntityTest {

    val entity = Entities.settings
    val model = Models.settings

    @Test
    fun `map entity to domain`() {
        // given
        // when
        val transformedEntity = entity.mapToDomain()
        // then
        assertEquals(model, transformedEntity)
    }
}
