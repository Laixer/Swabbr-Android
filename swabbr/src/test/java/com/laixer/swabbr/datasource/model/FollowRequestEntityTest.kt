@file:Suppress("IllegalIdentifier")

package com.laixer.swabbr.datasource.model

import com.laixer.swabbr.followStatusEntity
import org.junit.Assert.assertEquals
import org.junit.Test

class FollowRequestEntityTest {

    @Test
    fun `map entity to domain`() {
        // given

        // when
        val model = followStatusEntity.mapToDomain()

        // then
        assertEquals(model, followStatusEntity.status)
    }
}
