@file:Suppress("IllegalIdentifier")

package com.laixer.sample.datasource.cache

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.laixer.cache.ReactiveCache
import com.laixer.sample.domain.model.Vlog
import com.laixer.sample.vlog
import io.reactivex.Single
import org.junit.Before
import org.junit.Test

class VlogCacheDataSourceImplTest {

    private lateinit var dataSource: VlogCacheDataSourceImpl

    private val mockCache: ReactiveCache<List<Vlog>> = mock()

    val key = "Vlog List"

    private val vlogId = vlog.id

    private val cacheItem = vlog.copy(id = "cache")
    private val remoteItem = vlog.copy(id = "remote")

    private val cacheList = listOf(cacheItem)
    private val remoteList = listOf(remoteItem)

    private val throwable = Throwable()

    @Before
    fun setUp() {
        dataSource = VlogCacheDataSourceImpl(mockCache)
    }

    @Test
    fun `get vlogs cache success`() {
        // given
        whenever(mockCache.load(key)).thenReturn(Single.just(cacheList))

        // when
        val test = dataSource.get().test()

        // then
        verify(mockCache).load(key)
        test.assertValue(cacheList)
    }

    @Test
    fun `get vlogs cache fail`() {
        // given
        whenever(mockCache.load(key)).thenReturn(Single.error(throwable))

        // when
        val test = dataSource.get().test()

        // then
        verify(mockCache).load(key)
        test.assertError(throwable)
    }

    @Test
    fun `get vlog cache success`() {
        // given
        whenever(mockCache.load(key)).thenReturn(Single.just(cacheList))

        // when
        val test = dataSource.get(vlogId).test()

        // then
        verify(mockCache).load(key)
        test.assertValue(cacheItem)
    }

    @Test
    fun `get vlog cache fail`() {
        // given
        whenever(mockCache.load(key)).thenReturn(Single.error(throwable))

        // when
        val test = dataSource.get(vlogId).test()

        // then
        verify(mockCache).load(key)
        test.assertError(throwable)
    }

    @Test
    fun `set vlogs cache success`() {
        // given
        whenever(mockCache.save(key, remoteList)).thenReturn(Single.just(remoteList))

        // when
        val test = dataSource.set(remoteList).test()

        // then
        verify(mockCache).save(key, remoteList)
        test.assertValue(remoteList)
    }

    @Test
    fun `set vlogs cache fail`() {
        // given
        whenever(mockCache.save(key, remoteList)).thenReturn(Single.error(throwable))

        // when
        val test = dataSource.set(remoteList).test()

        // then
        verify(mockCache).save(key, remoteList)
        test.assertError(throwable)
    }

    @Test
    fun `set vlog cache success`() {
        // given
        whenever(mockCache.load(key)).thenReturn(Single.just(emptyList()))
        whenever(mockCache.save(key, remoteList)).thenReturn(Single.just(remoteList))

        // when
        val test = dataSource.set(remoteItem).test()

        // then
        verify(mockCache).save(key, remoteList)
        test.assertValue(remoteItem)
    }

    @Test
    fun `set vlog cache fail`() {
        // given
        whenever(mockCache.load(key)).thenReturn(Single.just(emptyList()))
        whenever(mockCache.save(key, remoteList)).thenReturn(Single.error(throwable))

        // when
        val test = dataSource.set(remoteItem).test()

        // then
        verify(mockCache).save(key, remoteList)
        test.assertError(throwable)
    }
}
