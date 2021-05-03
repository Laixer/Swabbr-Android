package com.laixer.swabbr.presentation.model

import com.laixer.swabbr.Items
import com.laixer.swabbr.Models
import org.junit.Assert.assertEquals
import org.junit.Test

class SettingsItemTest {

    private val item = Items.settings
    private val model = Models.settings

    @Test
    fun `map presentation to domain`() {
        // given
        // when
        val transformedItem = item.mapToDomain()
        // then
        assertEquals(model, transformedItem)
    }
}
