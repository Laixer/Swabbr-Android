package com.laixer.swabbr.datasource.model

import com.laixer.swabbr.Entities
import com.laixer.swabbr.Models
import org.junit.Assert.assertEquals
import org.junit.Test

class RegistrationEntityTest {

    val entity = Entities.registration
    val model = Models.registration

    @Test
    fun `map entity to domain`() {
        // given
        // when
        val transformedEntity = entity.mapToDomain()
        // then
        assertEquals(model, transformedEntity)
    }
}
