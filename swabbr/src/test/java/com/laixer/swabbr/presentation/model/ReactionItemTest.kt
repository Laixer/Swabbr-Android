package com.laixer.swabbr.presentation.model

import com.laixer.swabbr.reaction
import com.laixer.swabbr.user
import org.junit.Assert.assertTrue
import org.junit.Test

class ReactionItemTest {

    @Test
    fun `map domain to presentation`() {
        // given
        val userReaction = Pair(user, reaction)
        // when
        val reactionItem = listOf(userReaction).mapToPresentation().first()
        // then
        assertTrue(reactionItem.vlogId == reaction.vlogId)
        assertTrue(reactionItem.userId == reaction.userId)
        assertTrue(reactionItem.id == reaction.id)
        assertTrue(reactionItem.duration == reaction.duration)
        assertTrue(reactionItem.postDate == reaction.postDate)
        assertTrue(reactionItem.nickname == user.nickname)
    }
}
