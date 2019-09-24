@file:Suppress("IllegalIdentifier")

package com.laixer.sample.datasource.model

import com.laixer.sample.userEntity
import org.junit.Assert.assertTrue
import org.junit.Test

class UserEntityTest {

    @Test
    fun `map entity to domain`() {
        // given

        // when
        val model = userEntity.mapToDomain()

        // then
        assertTrue(model.id == userEntity.id)
        assertTrue(model.firstName == userEntity.firstName)
        assertTrue(model.lastName == userEntity.lastName)
        assertTrue(model.gender == userEntity.gender)
        assertTrue(model.country == userEntity.country)
        assertTrue(model.email == userEntity.email)
        assertTrue(model.timezone == userEntity.timezone)
        assertTrue(model.totalVlogs == userEntity.totalVlogs)
        assertTrue(model.totalFollowers == userEntity.totalFollowers)
        assertTrue(model.totalFollowing == userEntity.totalFollowing)
        assertTrue(model.nickname == userEntity.nickname)
        assertTrue(model.profileImageUrl == userEntity.profileImageUrl)
        assertTrue(model.birthdate == userEntity.birthdate)
        assertTrue(model.longitude == userEntity.longitude)
        assertTrue(model.latitude == userEntity.latitude)
    }
}
