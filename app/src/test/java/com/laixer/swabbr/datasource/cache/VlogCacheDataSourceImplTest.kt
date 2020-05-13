package com.laixer.swabbr.datasource.cache

import com.laixer.cache.ReactiveCache
import com.laixer.swabbr.Models
import com.laixer.swabbr.data.datasource.cache.VlogCacheDataSourceImpl
import com.laixer.swabbr.domain.model.Vlog
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import org.junit.Before
import org.junit.Test

class VlogCacheDataSourceImplTest {

    private lateinit var dataSource: VlogCacheDataSourceImpl
    private val key by lazy { dataSource.key }
    private val recommendedKey by lazy { dataSource.recommendedKey }

    private val mockCache: ReactiveCache<List<Vlog>> = mock()

    private val vlogId = Models.vlog.id
    private val model = Models.vlog
    private val list = listOf(model)
    private val throwable = Throwable()

    @Before
    fun setUp() {
        dataSource = VlogCacheDataSourceImpl(mockCache)
    }

    @Test
    fun `get recommended vlogs cache success`() {
        // given
        whenever(mockCache.load(recommendedKey)).thenReturn(Single.just(list))
        // when
        val test = dataSource.getRecommendedVlogs().test()
        // then
        verify(mockCache).load(recommendedKey)
        test.assertValue(list)
    }

    @Test
    fun `get recommended vlogs cache fail`() {
        // given
        whenever(mockCache.load(recommendedKey)).thenReturn(Single.error(throwable))
        // when
        val test = dataSource.getRecommendedVlogs().test()
        // then
        verify(mockCache).load(recommendedKey)
        test.assertError(throwable)
    }

    @Test
    fun `get vlog cache success`() {
        // given
        whenever(mockCache.load(key)).thenReturn(Single.just(list))
        // when
        val test = dataSource.get(vlogId).test()
        // then
        verify(mockCache).load(key)
        test.assertValue(model)
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
    fun `set recommended vlogs cache success`() {
        // given
        whenever(mockCache.save(recommendedKey, list)).thenReturn(Single.just(list))
        // when
        val test = dataSource.setRecommendedVlogs(list).test()
        // then
        verify(mockCache).save(recommendedKey, list)
        test.assertValue(list)
    }

    @Test
    fun `set recommended vlogs cache fail`() {
        // given
        whenever(mockCache.save(recommendedKey, list)).thenReturn(Single.error(throwable))
        // when
        val test = dataSource.setRecommendedVlogs(list).test()
        // then
        verify(mockCache).save(recommendedKey, list)
        test.assertError(throwable)
    }

    @Test
    fun `set vlog cache success`() {
        // given
        whenever(mockCache.load(key)).thenReturn(Single.just(emptyList()))
        whenever(mockCache.save(key, list)).thenReturn(Single.just(list))
        // when
        val test = dataSource.set(list).test()
        // then
        verify(mockCache).save(key, list)
        test.assertValue(list)
    }

    @Test
    fun `set vlog cache fail`() {
        // given
        whenever(mockCache.load(key)).thenReturn(Single.just(emptyList()))
        whenever(mockCache.save(key, list)).thenReturn(Single.error(throwable))
        // when
        val test = dataSource.set(list).test()
        // then
        verify(mockCache).save(key, list)
        test.assertError(throwable)
    }
}
