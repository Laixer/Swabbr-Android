@file:Suppress("IllegalIdentifier")

package com.laixer.sample.presentation.model

import com.laixer.sample.reaction
import org.junit.Assert.assertTrue
import org.junit.Test

class ReactionItemTest {

    @Test
    fun `map domain to presentation`() {
        // given

        // when
        val reactionItem = listOf(reaction).mapToPresentation().first()

        // then
        assertTrue(reactionItem.vlogId == reaction.vlogId)
        assertTrue(reactionItem.userId == reaction.userId)
        assertTrue(reactionItem.id == reaction.id)
        assertTrue(reactionItem.duration == reaction.duration)
        assertTrue(reactionItem.postDate == reaction.postDate)
    }
}
