package com.laixer.swabbr.datasource.model

import com.laixer.swabbr.followRequest
import com.laixer.swabbr.followRequestEntity
import org.junit.Assert.assertEquals
import org.junit.Test

class FollowRequestEntityTest {

    @Test
    fun `map entity to domain`() {
        // given
        // when
        val model = followRequestEntity.mapToDomain()
        // then
        assertEquals(model, followRequest)
    }
}
