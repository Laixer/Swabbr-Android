package com.laixer.swabbr.presentation.model

import com.laixer.swabbr.loginItem
import org.junit.Assert.assertEquals
import org.junit.Test

class LoginItemTest {

    @Test
    fun `map presentation to domain`() {
        // given
        val loginItem = loginItem
        // when
        val login = loginItem.mapToDomain()
        // then
        assertEquals(login.username, loginItem.username)
        assertEquals(login.password, loginItem.password)
    }
}
