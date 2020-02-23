@file:Suppress("IllegalIdentifier")

package com.laixer.swabbr.data.repository

import com.laixer.swabbr.data.datasource.VlogCacheDataSource
import com.laixer.swabbr.data.datasource.VlogRemoteDataSource
import com.laixer.swabbr.vlog
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import org.junit.Before
import org.junit.Test

class VlogRepositoryImplTest {

    private lateinit var repository: VlogRepositoryImpl
    private val mockCacheDataSource: VlogCacheDataSource = mock()
    private val mockRemoteDataSource: VlogRemoteDataSource = mock()
    private val vlogId = vlog.id
    private val cacheItem = vlog.copy(id = "cache")
    private val remoteItem = vlog.copy(id = "remote")
    private val cacheList = listOf(cacheItem)
    private val remoteList = listOf(remoteItem)
    private val cacheThrowable = Throwable()
    private val remoteThrowable = Throwable()

    @Before
    fun setUp() {
        repository = VlogRepositoryImpl(mockCacheDataSource, mockRemoteDataSource)
    }

    @Test
    fun `get vlogs cache success`() {
        // given
        whenever(mockCacheDataSource.get()).thenReturn(Single.just(cacheList))
        // when
        val test = repository.get(false).test()
        // then
        verify(mockCacheDataSource).get()
        test.assertValue(cacheList)
    }

    @Test
    fun `get vlogs cache fail fallback remote succeeds`() {
        // given
        whenever(mockCacheDataSource.get()).thenReturn(Single.error(cacheThrowable))
        whenever(mockRemoteDataSource.get()).thenReturn(Single.just(remoteList))
        whenever(mockCacheDataSource.set(remoteList)).thenReturn(Single.just(remoteList))
        // when
        val test = repository.get(false).test()
        // then
        verify(mockCacheDataSource).get()
        verify(mockRemoteDataSource).get()
        verify(mockCacheDataSource).set(remoteList)
        test.assertValue(remoteList)
    }

    @Test
    fun `get vlogs cache fail fallback remote fails`() {
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
    fun `get vlogs remote success`() {
        // given
        whenever(mockRemoteDataSource.get()).thenReturn(Single.just(remoteList))
        whenever(mockCacheDataSource.set(remoteList)).thenReturn(Single.just(remoteList))
        // when
        val test = repository.get(true).test()
        // then
        verify(mockRemoteDataSource).get()
        verify(mockCacheDataSource).set(remoteList)
        test.assertValue(remoteList)
    }

    @Test
    fun `get vlogs remote fail`() {
        // given
        whenever(mockRemoteDataSource.get()).thenReturn(Single.error(remoteThrowable))
        // when
        val test = repository.get(true).test()
        // then
        verify(mockRemoteDataSource).get()
        test.assertError(remoteThrowable)
    }

    @Test
    fun `get vlog cache success`() {
        // given
        whenever(mockCacheDataSource.get(vlogId)).thenReturn(Single.just(cacheItem))
        // when
        val test = repository.get(vlogId, false).test()
        // then
        verify(mockCacheDataSource).get(vlogId)
        test.assertValue(cacheItem)
    }

    @Test
    fun `get vlog cache fail fallback remote succeeds`() {
        // given
        whenever(mockCacheDataSource.get(vlogId)).thenReturn(Single.error(cacheThrowable))
        whenever(mockRemoteDataSource.get(vlogId)).thenReturn(Single.just(remoteItem))
        whenever(mockCacheDataSource.set(remoteItem)).thenReturn(Single.just(remoteItem))
        // when
        val test = repository.get(vlogId, false).test()
        // then
        verify(mockCacheDataSource).get(vlogId)
        verify(mockRemoteDataSource).get(vlogId)
        verify(mockCacheDataSource).set(remoteItem)
        test.assertValue(remoteItem)
    }

    @Test
    fun `get vlog cache fail fallback remote fails`() {
        // given
        whenever(mockCacheDataSource.get(vlogId)).thenReturn(Single.error(cacheThrowable))
        whenever(mockRemoteDataSource.get(vlogId)).thenReturn(Single.error(remoteThrowable))
        // when
        val test = repository.get(vlogId, false).test()
        // then
        verify(mockCacheDataSource).get(vlogId)
        verify(mockRemoteDataSource).get(vlogId)
        test.assertError(remoteThrowable)
    }

    @Test
    fun `get vlog remote success`() {
        // given
        whenever(mockRemoteDataSource.get(vlogId)).thenReturn(Single.just(remoteItem))
        whenever(mockCacheDataSource.set(remoteItem)).thenReturn(Single.just(remoteItem))
        // when
        val test = repository.get(vlogId, true).test()
        // then
        verify(mockRemoteDataSource).get(vlogId)
        verify(mockCacheDataSource).set(remoteItem)
        test.assertValue(remoteItem)
    }

    @Test
    fun `get vlog remote fail`() {
        // given
        whenever(mockRemoteDataSource.get()).thenReturn(Single.error(remoteThrowable))
        // when
        val test = repository.get(true).test()
        // then
        verify(mockRemoteDataSource).get()
        test.assertError(remoteThrowable)
    }
}
