@file:Suppress("IllegalIdentifier")

package com.laixer.swabbr.presentation.model

import com.laixer.swabbr.settings
import org.junit.Assert.assertEquals
import org.junit.Test

class SettingsItemTest {

    @Test
    fun `map domain to presentation`() {
        // given
        val settings = settings

        // when
        val settingsItem = settings.mapToPresentation()

        // then
        assertEquals(settingsItem.private, settings.private)
        assertEquals(settingsItem.dailyVlogRequestLimit, settings.dailyVlogRequestLimit)
        assertEquals(settingsItem.followMode, settings.followMode)
    }
}
