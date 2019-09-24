@file:Suppress("IllegalIdentifier")

package com.laixer.sample.datasource.model

import com.laixer.sample.reactionEntity
import org.junit.Assert.assertTrue
import org.junit.Test

class ReactionEntityTest {

    @Test
    fun `map entity to domain`() {
        // given

        // when
        val model = reactionEntity.mapToDomain()

        // then
        assertTrue(model.userId == reactionEntity.userId)
        assertTrue(model.vlogId == reactionEntity.vlogId)
        assertTrue(model.id == reactionEntity.id)
        assertTrue(model.duration == reactionEntity.duration)
        assertTrue(model.postDate == reactionEntity.postDate)
    }
}
