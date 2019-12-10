@file:Suppress("IllegalIdentifier")

package com.laixer.swabbr.datasource.model

import com.laixer.swabbr.reactionEntity
import org.junit.Assert.assertEquals
import org.junit.Test

class ReactionEntityTest {

    @Test
    fun `map entity to domain`() {
        // given

        // when
        val model = reactionEntity.mapToDomain()

        // then
        assertEquals(model.userId, reactionEntity.userId)
        assertEquals(model.vlogId, reactionEntity.vlogId)
        assertEquals(model.id, reactionEntity.id)
        assertEquals(model.duration, reactionEntity.duration)
        assertEquals(model.postDate, reactionEntity.postDate)
    }
}
