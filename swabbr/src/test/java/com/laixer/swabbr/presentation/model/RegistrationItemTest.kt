package com.laixer.swabbr.presentation.model

import com.laixer.swabbr.registrationItem
import org.junit.Assert.assertEquals
import org.junit.Test

class RegistrationItemTest {

    @Test
    fun `map presentation to domain`() {
        // given
        val registrationItem = registrationItem
        // when
        val registration = registrationItem.mapToDomain()
        // then
        assertEquals(registration.firstName, registrationItem.firstName)
        assertEquals(registration.lastName, registrationItem.lastName)
        assertEquals(registration.gender, registrationItem.gender)
        assertEquals(registration.country, registrationItem.country)
        assertEquals(registration.email, registrationItem.email)
        assertEquals(registration.password, registrationItem.password)
        assertEquals(registration.birthdate, registrationItem.birthdate)
        assertEquals(registration.timezone, registrationItem.timezone)
        assertEquals(registration.nickname, registrationItem.nickname)
        assertEquals(registration.profileImageUrl, registrationItem.profileImageUrl)
        assertEquals(registration.isPrivate, registrationItem.isPrivate)
        assertEquals(registration.phoneNumber, registrationItem.phoneNumber)
    }
}
