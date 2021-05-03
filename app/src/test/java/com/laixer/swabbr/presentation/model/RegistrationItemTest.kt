package com.laixer.swabbr.presentation.model

import com.laixer.swabbr.Items
import com.laixer.swabbr.Models
import org.junit.Assert.assertEquals
import org.junit.Test

class RegistrationItemTest {

    private val item = Items.registration
    private val model = Models.registration

    @Test
    fun `map presentation to domain`() {
        // given
        // when
        val transformedItem = item.mapToDomain()
        // then
        assertEquals(model, transformedItem)
    }
}
