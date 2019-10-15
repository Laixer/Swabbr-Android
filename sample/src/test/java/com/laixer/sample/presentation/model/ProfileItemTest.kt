@file:Suppress("IllegalIdentifier")

package com.laixer.sample.presentation.model

import com.laixer.sample.user
import org.junit.Assert.assertEquals
import org.junit.Test

class ProfileItemTest {

    @Test
    fun `map domain to presentation`() {
        // given
        val user = user

        // when
        val profileItem = user.mapToPresentation()

        // then
        assertEquals(profileItem.id, user.id)
        assertEquals(profileItem.nickname, user.nickname)
        assertEquals(profileItem.firstName, user.firstName)
        assertEquals(profileItem.lastName, user.lastName)
    }
}
