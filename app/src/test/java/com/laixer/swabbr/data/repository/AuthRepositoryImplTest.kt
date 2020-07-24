package com.laixer.swabbr.data.repository

import com.laixer.swabbr.Items
import com.laixer.swabbr.Models
import com.laixer.swabbr.data.datasource.AuthCacheDataSource
import com.laixer.swabbr.data.datasource.AuthRemoteDataSource
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import org.junit.Before
import org.junit.Test

class AuthRepositoryImplTest {

    private lateinit var repository: AuthRepositoryImpl
    private val mockAuthCacheDataSource: AuthCacheDataSource = mock()
    private val mockAuthRemoteDataSource: AuthRemoteDataSource = mock()
    private val throwable = Throwable()

    private val login = Models.login
    private val registration = Models.registration

    private val authModel = Models.authUser

    @Before
    fun setUp() {
        repository = AuthRepositoryImpl(
            mockAuthCacheDataSource,
            mockAuthRemoteDataSource
        )
    }

    @Test
    fun `login success`() {
        // given
        whenever(mockAuthRemoteDataSource.login(login)).thenReturn(Single.just(authModel))
        whenever(mockAuthCacheDataSource.set(authModel)).thenReturn(Single.just(authModel))

        // when
        val test = repository.login(login).test()
        // then
        verify(mockAuthRemoteDataSource).login(login)
        verify(mockAuthCacheDataSource).set(authModel)
        test.assertValue(authModel)
    }

    @Test
    fun `login fail`() {
        // given
        whenever(mockAuthRemoteDataSource.login(login)).thenReturn(Single.error(throwable))
        // when
        val test = repository.login(login).test()
        // then
        verify(mockAuthRemoteDataSource).login(login)
        test.assertError(throwable)
    }

    @Test
    fun `registration success`() {
        // given
        whenever(mockAuthRemoteDataSource.register(registration)).thenReturn(Single.just(authModel))
        whenever(mockAuthCacheDataSource.set(authModel)).thenReturn(Single.just(authModel))

        // when
        val test = repository.register(registration).test()
        // then
        verify(mockAuthRemoteDataSource).register(registration)
        verify(mockAuthCacheDataSource).set(authModel)
        test.assertValue(authModel)
    }

    @Test
    fun `registration fail`() {
        // given
        whenever(mockAuthRemoteDataSource.register(registration)).thenReturn(Single.error(throwable))
        // when
        val test = repository.register(registration).test()
        // then
        verify(mockAuthRemoteDataSource).register(registration)
        test.assertError(throwable)
    }
}