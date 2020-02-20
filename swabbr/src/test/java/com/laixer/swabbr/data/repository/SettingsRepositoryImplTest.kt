@file:Suppress("IllegalIdentifier")

package com.laixer.swabbr.data.repository

import com.laixer.swabbr.data.datasource.SettingsCacheDataSource
import com.laixer.swabbr.data.datasource.SettingsRemoteDataSource
import com.laixer.swabbr.settings
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import org.junit.Before
import org.junit.Test

class SettingsRepositoryImplTest {

    private lateinit var repository: SettingsRepositoryImpl
    private val mockCacheDataSource: SettingsCacheDataSource = mock()
    private val mockRemoteDataSource: SettingsRemoteDataSource = mock()
    private val cacheItem = settings
    private val remoteItem = settings
    private val cacheThrowable = Throwable()
    private val remoteThrowable = Throwable()

    @Before
    fun setUp() {
        repository = SettingsRepositoryImpl(mockCacheDataSource, mockRemoteDataSource)
    }

    @Test
    fun `get settings cache success`() {
        // given
        whenever(mockCacheDataSource.get()).thenReturn(Single.just(cacheItem))
        // when
        val test = repository.get(false).test()
        // then
        verify(mockCacheDataSource).get()
        test.assertValue(cacheItem)
    }

    @Test
    fun `get settings cache fail fallback remote succeeds`() {
        // given
        whenever(mockCacheDataSource.get()).thenReturn(Single.error(cacheThrowable))
        whenever(mockRemoteDataSource.get()).thenReturn(Single.just(remoteItem))
        whenever(mockCacheDataSource.set(remoteItem)).thenReturn(Single.just(remoteItem))
        // when
        val test = repository.get(false).test()
        // then
        verify(mockCacheDataSource).get()
        verify(mockRemoteDataSource).get()
        verify(mockCacheDataSource).set(remoteItem)
        test.assertValue(remoteItem)
    }

    @Test
    fun `get settings cache fail fallback remote fails`() {
        // given
        whenever(mockCacheDataSource.get()).thenReturn(Single.error(cacheThrowable))
        whenever(mockRemoteDataSource.get()).thenReturn(Single.error(remoteThrowable))
        // when
        val test = repository.get(false).test()
        // then
        verify(mockCacheDataSource).get()
        verify(mockRemoteDataSource).get()
        test.assertError(remoteThrowable)
    }

    @Test
    fun `get settings remote success`() {
        // given
        whenever(mockRemoteDataSource.get()).thenReturn(Single.just(remoteItem))
        whenever(mockCacheDataSource.set(remoteItem)).thenReturn(Single.just(remoteItem))
        // when
        val test = repository.get(true).test()
        // then
        verify(mockRemoteDataSource).get()
        verify(mockCacheDataSource).set(remoteItem)
        test.assertValue(remoteItem)
    }

    @Test
    fun `get settings remote fail`() {
        // given
        whenever(mockRemoteDataSource.get()).thenReturn(Single.error(remoteThrowable))
        // when
        val test = repository.get(true).test()
        // then
        verify(mockRemoteDataSource).get()
        test.assertError(remoteThrowable)
    }
}
