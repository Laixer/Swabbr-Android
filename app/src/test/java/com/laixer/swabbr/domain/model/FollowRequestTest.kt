package com.laixer.swabbr.domain.model

import com.laixer.swabbr.Entities
import com.laixer.swabbr.Models
import com.laixer.swabbr.datasource.model.mapToData
import org.junit.Assert.assertEquals
import org.junit.Test

class FollowRequestTest {

    val entity = Entities.followRequest
    val model = Models.followRequest

    @Test
    fun `map model to data`() {
        // given
        // when
        val transformedModel = model.mapToData()
        // then
        assertEquals(entity, transformedModel)
    }
}
