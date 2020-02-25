package com.laixer.swabbr.datasource.model

import com.laixer.swabbr.registration
import org.junit.Assert.assertEquals
import org.junit.Test

class RegistrationEntityTest {

    @Test
    fun `map entity to domain`() {
        // given
        // when
        val item = registration.mapToData()
        // then
        assertEquals(item.firstName, registration.firstName)
        assertEquals(item.lastName, registration.lastName)
        assertEquals(item.gender, registration.gender)
        assertEquals(item.country, registration.country)
        assertEquals(item.email, registration.email)
        assertEquals(item.password, registration.password)
        assertEquals(item.birthdate, registration.birthdate.toString())
        assertEquals(item.timezone, registration.timezone)
        assertEquals(item.nickname, registration.nickname)
        assertEquals(item.profileImageUrl, registration.profileImageUrl)
        assertEquals(item.isPrivate, registration.isPrivate)
        assertEquals(item.phoneNumber, registration.phoneNumber)
    }
}
