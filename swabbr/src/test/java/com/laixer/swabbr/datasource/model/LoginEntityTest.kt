@file:Suppress("IllegalIdentifier")

package com.laixer.swabbr.datasource.model

import com.laixer.swabbr.login
import org.junit.Assert.assertEquals
import org.junit.Test

class LoginEntityTest {

    @Test
    fun `map entity to domain`() {
        // given

        // when
        val item = login.mapToData()

        // then
        assertEquals(item.username, login.username)
        assertEquals(item.password, login.password)
    }
}
