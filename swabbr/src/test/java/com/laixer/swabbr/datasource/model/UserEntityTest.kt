package com.laixer.swabbr.datasource.model

import com.laixer.swabbr.Entities
import com.laixer.swabbr.Models
import org.junit.Assert.assertEquals
import org.junit.Test

class UserEntityTest {

    val entity = Entities.user
    val model = Models.user

    @Test
    fun `map entity to domain`() {
        // given
        // when
        val transformedEntity = entity.mapToDomain()
        // then
        assertEquals(model, transformedEntity)
    }
}
