@file:Suppress("IllegalIdentifier")

package com.laixer.swabbr.data.repository

import com.laixer.swabbr.data.datasource.AuthCacheDataSource
import com.laixer.swabbr.data.datasource.AuthRemoteDataSource
import com.laixer.swabbr.domain.model.AuthUser
import com.laixer.swabbr.login
import com.laixer.swabbr.registration
import com.laixer.swabbr.settings
import com.laixer.swabbr.user
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

    private val cacheAuthItem = Pair("token", "userId")
    private val remoteAuthItem = AuthUser("token", user, settings)

    private val response = AuthUser("token", user, settings)

    private val throwable = Throwable()

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
        whenever(mockAuthRemoteDataSource.login(login)).thenReturn(Single.just(remoteAuthItem))
        whenever(mockAuthCacheDataSource.set(cacheAuthItem)).thenReturn(Single.just(cacheAuthItem))

        // when
        val test = repository.login(login).test()

        // then
        verify(mockAuthRemoteDataSource).login(login)
        verify(mockAuthCacheDataSource).set(cacheAuthItem)
        test.assertValue(response)
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
        whenever(mockAuthRemoteDataSource.register(registration)).thenReturn(Single.just(remoteAuthItem))
        whenever(mockAuthCacheDataSource.set(cacheAuthItem)).thenReturn(Single.just(cacheAuthItem))

        // when
        val test = repository.register(registration).test()

        // then
        verify(mockAuthRemoteDataSource).register(registration)
        verify(mockAuthCacheDataSource).set(cacheAuthItem)
        test.assertValue(response)
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
