@file:Suppress("IllegalIdentifier")

package com.laixer.swabbr.datasource.cache

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.laixer.cache.ReactiveCache
import com.laixer.swabbr.domain.model.Reaction
import com.laixer.swabbr.reaction
import com.laixer.swabbr.vlog
import io.reactivex.Single
import org.junit.Before
import org.junit.Test

class ReactionCacheDataSourceImplTest {

    private lateinit var dataSource: ReactionCacheDataSourceImpl

    private val mockCache: ReactiveCache<List<Reaction>> = mock()

    val key = "Reaction List"

    private val vlogId = vlog.vlogId

    private val cacheItem = reaction.copy(id = "cache")
    private val remoteItem = reaction.copy(id = "remote")

    private val cacheList = listOf(cacheItem)
    private val remoteList = listOf(remoteItem)

    private val throwable = Throwable()

    @Before
    fun setUp() {
        dataSource = ReactionCacheDataSourceImpl(mockCache)
    }

    @Test
    fun `get reactions cache success`() {
        // given
        whenever(mockCache.load(key + vlogId)).thenReturn(Single.just(cacheList))

        // when
        val test = dataSource.get(vlogId).test()

        // then
        verify(mockCache).load(key + vlogId)
        test.assertValue(cacheList)
    }

    @Test
    fun `get reactions cache fail`() {
        // given
        whenever(mockCache.load(key + vlogId)).thenReturn(Single.error(throwable))

        // when
        val test = dataSource.get(vlogId).test()

        // then
        verify(mockCache).load(key + vlogId)
        test.assertError(throwable)
    }

    @Test
    fun `set reactions cache success`() {
        // given
        whenever(mockCache.save(key + vlogId, remoteList)).thenReturn(Single.just(remoteList))

        // when
        val test = dataSource.set(vlogId, remoteList).test()

        // then
        verify(mockCache).save(key + vlogId, remoteList)
        test.assertValue(remoteList)
    }

    @Test
    fun `set reactions cache fail`() {
        // given
        whenever(mockCache.save(key + vlogId, remoteList)).thenReturn(Single.error(throwable))

        // when
        val test = dataSource.set(vlogId, remoteList).test()

        // then
        verify(mockCache).save(key + vlogId, remoteList)
        test.assertError(throwable)
    }
}
