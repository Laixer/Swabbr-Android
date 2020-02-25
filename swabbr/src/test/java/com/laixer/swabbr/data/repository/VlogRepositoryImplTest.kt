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
    fun `get featured vlogs cache success`() {
        // given
        whenever(mockCacheDataSource.getFeaturedVlogs()).thenReturn(Single.just(cacheList))
        // when
        val test = repository.getFeaturedVlogs(false).test()
        // then
        verify(mockCacheDataSource).getFeaturedVlogs()
        test.assertValue(cacheList)
    }

    @Test
    fun `get featured vlogs cache fail fallback remote succeeds`() {
        // given
        whenever(mockCacheDataSource.getFeaturedVlogs()).thenReturn(Single.error(cacheThrowable))
        whenever(mockRemoteDataSource.getFeaturedVlogs()).thenReturn(Single.just(remoteList))
        whenever(mockCacheDataSource.setFeaturedVlogs(remoteList)).thenReturn(Single.just(remoteList))
        // when
        val test = repository.getFeaturedVlogs(false).test()
        // then
        verify(mockCacheDataSource).getFeaturedVlogs()
        verify(mockRemoteDataSource).getFeaturedVlogs()
        verify(mockCacheDataSource).setFeaturedVlogs(remoteList)
        test.assertValue(remoteList)
    }

    @Test
    fun `get featured vlogs cache fail fallback remote fails`() {
        // given
        whenever(mockCacheDataSource.getFeaturedVlogs()).thenReturn(Single.error(cacheThrowable))
        whenever(mockRemoteDataSource.getFeaturedVlogs()).thenReturn(Single.error(remoteThrowable))
        // when
        val test = repository.getFeaturedVlogs(false).test()
        // then
        verify(mockCacheDataSource).getFeaturedVlogs()
        verify(mockRemoteDataSource).getFeaturedVlogs()
        test.assertError(remoteThrowable)
    }

    @Test
    fun `get featured vlogs remote success`() {
        // given
        whenever(mockRemoteDataSource.getFeaturedVlogs()).thenReturn(Single.just(remoteList))
        whenever(mockCacheDataSource.setFeaturedVlogs(remoteList)).thenReturn(Single.just(remoteList))
        // when
        val test = repository.getFeaturedVlogs(true).test()
        // then
        verify(mockRemoteDataSource).getFeaturedVlogs()
        verify(mockCacheDataSource).setFeaturedVlogs(remoteList)
        test.assertValue(remoteList)
    }

    @Test
    fun `get featured vlogs remote fail`() {
        // given
        whenever(mockRemoteDataSource.getFeaturedVlogs()).thenReturn(Single.error(remoteThrowable))
        // when
        val test = repository.getFeaturedVlogs(true).test()
        // then
        verify(mockRemoteDataSource).getFeaturedVlogs()
        test.assertError(remoteThrowable)
    }

    @Test
    fun `get single vlog cache success`() {
        // given
        whenever(mockCacheDataSource.get(vlogId)).thenReturn(Single.just(cacheItem))
        // when
        val test = repository.get(vlogId, false).test()
        // then
        verify(mockCacheDataSource).get(vlogId)
        test.assertValue(cacheItem)
    }

    @Test
    fun `get single vlog cache fail fallback remote succeeds`() {
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
    fun `get single vlog cache fail fallback remote fails`() {
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
    fun `get single vlog remote success`() {
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
    fun `get single vlog remote fail`() {
        // given
        whenever(mockRemoteDataSource.get(vlogId)).thenReturn(Single.error(remoteThrowable))
        // when
        val test = repository.get(vlogId, true).test()
        // then
        verify(mockRemoteDataSource).get(vlogId)
        test.assertError(remoteThrowable)
    }
}
