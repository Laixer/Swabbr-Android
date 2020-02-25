package com.laixer.swabbr.datasource.remote

import com.laixer.swabbr.authenticatedUserEntity
import com.laixer.swabbr.datasource.model.mapToDomain
import com.laixer.swabbr.domain.model.AuthUser
import com.laixer.swabbr.login
import com.laixer.swabbr.loginEntity
import com.laixer.swabbr.registration
import com.laixer.swabbr.registrationEntity
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
        whenever(mockApi.login(loginEntity)).thenReturn(Single.just(authenticatedUserEntity))
        // when
        val test = dataSource.login(login).test()
        // then
        verify(mockApi).login(loginEntity)
        test.assertValue(
            AuthUser(
                authenticatedUserEntity.accessToken,
                authenticatedUserEntity.user.mapToDomain(),
                authenticatedUserEntity.userSettings.mapToDomain()
            )
        )
    }

    @Test
    fun `login remote fail`() {
        // given
        whenever(mockApi.login(loginEntity)).thenReturn(Single.error(throwable))
        // when
        val test = dataSource.login(login).test()
        // then
        verify(mockApi).login(loginEntity)
        test.assertError(throwable)
    }

    @Test
    fun `registration remote success`() {
        // given
        whenever(mockApi.register(registrationEntity)).thenReturn(Single.just(authenticatedUserEntity))
        // when
        val test = dataSource.register(registration).test()
        // then
        verify(mockApi).register(registrationEntity)
        test.assertValue(
            AuthUser(
                authenticatedUserEntity.accessToken,
                authenticatedUserEntity.user.mapToDomain(),
                authenticatedUserEntity.userSettings.mapToDomain()
            )
        )
    }

    @Test
    fun `registration remote fail`() {
        // given
        whenever(mockApi.register(registrationEntity)).thenReturn(Single.error(throwable))
        // when
        val test = dataSource.register(registration).test()
        // then
        verify(mockApi).register(registrationEntity)
        test.assertError(throwable)
    }
}
