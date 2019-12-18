@file:Suppress("IllegalIdentifier")

package com.laixer.swabbr.datasource.remote

import com.laixer.swabbr.authenticatedUserEntity
import com.laixer.swabbr.datasource.model.mapToDomain
import com.laixer.swabbr.login
import com.laixer.swabbr.registration
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import org.junit.Before
import org.junit.Test

class AuthRemoteDataSourceImplTest {

    private lateinit var dataSource: AuthRemoteDataSourceImpl

    private val mockApi: AuthApi = mock()

    private val throwable = Throwable()

    @Before
    fun setUp() {
        dataSource = AuthRemoteDataSourceImpl(mockApi)
    }

    @Test
    fun `login remote success`() {
        // given
        whenever(mockApi.login()).thenReturn(Single.just(authenticatedUserEntity))

        // when
        val test = dataSource.login(login).test()

        // then
        verify(mockApi).login()
        test.assertValue(Triple(
            authenticatedUserEntity.accessToken,
            authenticatedUserEntity.user.mapToDomain(),
            authenticatedUserEntity.userSettings.mapToDomain()
        ))
    }

    @Test
    fun `login remote fail`() {
        // given
        whenever(mockApi.login()).thenReturn(Single.error(throwable))

        // when
        val test = dataSource.login(login).test()

        // then
        verify(mockApi).login()
        test.assertError(throwable)
    }

    @Test
    fun `registration remote success`() {
        // given
        whenever(mockApi.register()).thenReturn(Single.just(authenticatedUserEntity))

        // when
        val test = dataSource.register(registration).test()

        // then
        verify(mockApi).register()
        test.assertValue(Triple(
            authenticatedUserEntity.accessToken,
            authenticatedUserEntity.user.mapToDomain(),
            authenticatedUserEntity.userSettings.mapToDomain()
        ))
    }

    @Test
    fun `registration remote fail`() {
        // given
        whenever(mockApi.register()).thenReturn(Single.error(throwable))

        // when
        val test = dataSource.register(registration).test()

        // then
        verify(mockApi).register()
        test.assertError(throwable)
    }
}
