package com.laixer.swabbr.domain.model

import com.laixer.swabbr.Entities
import com.laixer.swabbr.Models
import com.laixer.swabbr.data.model.mapToData
import org.junit.Assert.assertEquals
import org.junit.Test

class LoginTest {

    val entity = Entities.login
    val model = Models.login

    @Test
    fun `map entity to domain`() {
        // given
        // when
        val transformedModel = model.mapToData()
        // then
        assertEquals(entity, transformedModel)
    }
}
