package com.laixer.swabbr.datasource.cache

import com.laixer.cache.ReactiveCache
import com.laixer.swabbr.Models
import com.laixer.swabbr.data.datasource.cache.ReactionCacheDataSourceImpl
import com.laixer.swabbr.domain.model.Reaction
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import org.junit.Before
import org.junit.Test

class ReactionCacheDataSourceImplTest {

    private lateinit var dataSource: ReactionCacheDataSourceImpl
    private val key by lazy { dataSource.key }

    private val mockCache: ReactiveCache<List<Reaction>> = mock()

    private val vlogId = Models.vlog.id
    private val model = Models.reaction

    private val list = listOf(model)

    private val throwable = Throwable()

    @Before
    fun setUp() {
        dataSource = ReactionCacheDataSourceImpl(mockCache)
    }

    @Test
    fun `get reactions cache success`() {
        // given
        whenever(mockCache.load(key + vlogId)).thenReturn(Single.just(list))
        // when
        val test = dataSource.get(vlogId).test()
        // then
        verify(mockCache).load(key + vlogId)
        test.assertValue(list)
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
        whenever(mockCache.save(key + vlogId, list)).thenReturn(Single.just(list))
        // when
        val test = dataSource.set(vlogId, list).test()
        // then
        verify(mockCache).save(key + vlogId, list)
        test.assertValue(list)
    }

    @Test
    fun `set reactions cache fail`() {
        // given
        whenever(mockCache.save(key + vlogId, list)).thenReturn(Single.error(throwable))
        // when
        val test = dataSource.set(vlogId, list).test()
        // then
        verify(mockCache).save(key + vlogId, list)
        test.assertError(throwable)
    }
}
