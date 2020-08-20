package com.laixer.swabbr.datasource.remote

import com.laixer.swabbr.Entities
import com.laixer.swabbr.Models
import com.laixer.swabbr.data.datasource.remote.AuthRemoteDataSourceImpl
import com.laixer.swabbr.data.datasource.model.remote.AuthApi
import com.laixer.swabbr.data.datasource.model.remote.SettingsApi
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import org.junit.Before
import org.junit.Test

class AuthRemoteDataSourceImplTest {

    private lateinit var dataSource: AuthRemoteDataSourceImpl
    private val mockAuthApi: AuthApi = mock()
    private val mockSettingsApi: SettingsApi = mock()

    private val throwable = Throwable()

    private val loginEntity = Entities.login
    private val loginModel = Models.login

    private val registerEntity = Entities.registration
    private val registerModel = Models.registration

    private val responseEntity = Entities.authUser
    private val responseModel = Models.authUser

    @Before
    fun setUp() {
        dataSource = AuthRemoteDataSourceImpl(mockAuthApi, mockSettingsApi)
    }

    @Test
    fun `login remote success`() {
        // given
        whenever(mockAuthApi.login(loginEntity)).thenReturn(Single.just(responseEntity))
        // when
        val test = dataSource.login(loginModel).test()
        // then
        verify(mockAuthApi).login(loginEntity)
        test.assertValue(responseModel)
    }

    @Test
    fun `login remote fail`() {
        // given
        whenever(mockAuthApi.login(loginEntity)).thenReturn(Single.error(throwable))
        // when
        val test = dataSource.login(loginModel).test()
        // then
        verify(mockAuthApi).login(loginEntity)
        test.assertError(throwable)
    }

    @Test
    fun `registration remote success`() {
        // given
        whenever(mockAuthApi.register(registerEntity)).thenReturn(Single.just(responseEntity))
        // when
        val test = dataSource.register(registerModel).test()
        // then
        verify(mockAuthApi).register(registerEntity)
        test.assertValue(responseModel)
    }

    @Test
    fun `registration remote fail`() {
        // given
        whenever(mockAuthApi.register(registerEntity)).thenReturn(Single.error(throwable))
        // when
        val test = dataSource.register(registerModel).test()
        // then
        verify(mockAuthApi).register(registerEntity)
        test.assertError(throwable)
    }
}
