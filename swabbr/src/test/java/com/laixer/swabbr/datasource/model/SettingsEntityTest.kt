package com.laixer.swabbr.datasource.model

import com.laixer.swabbr.settingsEntity
import org.junit.Assert.assertEquals
import org.junit.Test

class SettingsEntityTest {

    @Test
    fun `map entity to domain`() {
        // given
        // when
        val model = settingsEntity.mapToDomain()
        // then
        assertEquals(model.private, settingsEntity.private)
        assertEquals(model.dailyVlogRequestLimit, settingsEntity.dailyVlogRequestLimit)
        assertEquals(model.followMode, settingsEntity.followMode)
    }
}
