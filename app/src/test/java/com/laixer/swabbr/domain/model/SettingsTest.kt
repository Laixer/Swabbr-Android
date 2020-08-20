package com.laixer.swabbr.domain.model

import com.laixer.swabbr.Entities
import com.laixer.swabbr.Models
import com.laixer.swabbr.data.datasource.model.mapToData
import org.junit.Assert.assertEquals
import org.junit.Test

class SettingsTest {

    val entity = Entities.settings
    val model = Models.settings

    @Test
    fun `map entity to domain`() {
        // given
        // when
        val transformedModel = model.mapToData()
        // then
        assertEquals(entity, transformedModel)
    }
}
