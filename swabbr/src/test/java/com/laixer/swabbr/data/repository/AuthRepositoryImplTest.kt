@file:Suppress("IllegalIdentifier")

package com.laixer.swabbr.data.repository

import com.laixer.swabbr.data.datasource.AuthCacheDataSource
import com.laixer.swabbr.data.datasource.AuthRemoteDataSource
import com.laixer.swabbr.data.datasource.SettingsCacheDataSource
import com.laixer.swabbr.data.datasource.UserCacheDataSource
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
    private val mockUserCacheDataSource: UserCacheDataSource = mock()
    private val mockSettingsCacheDataSource: SettingsCacheDataSource = mock()
    private val mockRemoteDataSource: AuthRemoteDataSource = mock()

    private val cacheAuthItem = Pair("token", user)
    private val cacheUserItem = user
    private val cacheSettingsItem = settings
    private val remoteAuthItem = Triple("token", user, settings)

    private val response = Pair(Pair("token", user), settings)

    private val throwable = Throwable()

    @Before
    fun setUp() {
        repository = AuthRepositoryImpl(
            mockAuthCacheDataSource,
            mockUserCacheDataSource,
            mockSettingsCacheDataSource,
            mockRemoteDataSource
        )
    }

    @Test
    fun `login success`() {
        // given
        whenever(mockRemoteDataSource.login(login)).thenReturn(Single.just(remoteAuthItem))
        whenever(mockAuthCacheDataSource.set(cacheAuthItem)).thenReturn(Single.just(cacheAuthItem))
        whenever(mockUserCacheDataSource.set(cacheUserItem)).thenReturn(Single.just(cacheUserItem))
        whenever(mockSettingsCacheDataSource.set(cacheSettingsItem)).thenReturn(Single.just(cacheSettingsItem))

        // when
        val test = repository.login(login).test()

        // then
        verify(mockRemoteDataSource).login(login)
        verify(mockAuthCacheDataSource).set(cacheAuthItem)
        test.assertValue(response)
    }

    @Test
    fun `login fail`() {
        // given
        whenever(mockRemoteDataSource.login(login)).thenReturn(Single.error(throwable))

        // when
        val test = repository.login(login).test()

        // then
        verify(mockRemoteDataSource).login(login)
        test.assertError(throwable)
    }

    @Test
    fun `registration success`() {
        // given
        whenever(mockRemoteDataSource.register(registration)).thenReturn(Single.just(remoteAuthItem))
        whenever(mockAuthCacheDataSource.set(cacheAuthItem)).thenReturn(Single.just(cacheAuthItem))
        whenever(mockUserCacheDataSource.set(cacheUserItem)).thenReturn(Single.just(cacheUserItem))
        whenever(mockSettingsCacheDataSource.set(cacheSettingsItem)).thenReturn(Single.just(cacheSettingsItem))

        // when
        val test = repository.register(registration).test()

        // then
        verify(mockRemoteDataSource).register(registration)
        verify(mockAuthCacheDataSource).set(cacheAuthItem)
        test.assertValue(response)
    }

    @Test
    fun `registration fail`() {
        // given
        whenever(mockRemoteDataSource.register(registration)).thenReturn(Single.error(throwable))

        // when
        val test = repository.register(registration).test()

        // then
        verify(mockRemoteDataSource).register(registration)
        test.assertError(throwable)
    }
}
