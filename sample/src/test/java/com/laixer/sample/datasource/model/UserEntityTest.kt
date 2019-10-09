@file:Suppress("IllegalIdentifier")

package com.laixer.sample.datasource.model

import com.laixer.sample.userEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UserEntityTest {

    @Test
    fun `map entity to domain`() {
        // given

        // when
        val model = userEntity.mapToDomain()

        // then
        assertEquals(model.id, userEntity.id)
        assertEquals(model.firstName, userEntity.firstName)
        assertEquals(model.lastName, userEntity.lastName)
        assertEquals(model.gender, userEntity.gender)
        assertEquals(model.country, userEntity.country)
        assertEquals(model.email, userEntity.email)
        assertEquals(model.timezone, userEntity.timezone)
        assertEquals(model.totalVlogs, userEntity.totalVlogs)
        assertEquals(model.totalFollowers, userEntity.totalFollowers)
        assertEquals(model.totalFollowing, userEntity.totalFollowing)
        assertEquals(model.nickname, userEntity.nickname)
        assertEquals(model.profileImageUrl, userEntity.profileImageUrl)
        assertEquals(model.birthdate, userEntity.birthdate)
        assertTrue(model.longitude == userEntity.longitude)
        assertTrue(model.latitude == userEntity.latitude)
    }
}
