package com.laixer.swabbr.datasource.cache

import com.laixer.cache.MemoryCache
import com.laixer.cache.ReactiveCache
import com.laixer.swabbr.Models
import com.laixer.swabbr.data.cache.AuthCacheDataSourceImpl
import com.laixer.swabbr.domain.model.TokenWrapper
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import org.junit.Before
import org.junit.Test

class AuthCacheDataSourceImplTest {

    private lateinit var dataSource: AuthCacheDataSourceImpl
    private val key by lazy { dataSource.key }
    private val mockAuthCache: ReactiveCache<TokenWrapper> = mock()
    private val mockAuthMemory: MemoryCache<TokenWrapper> = mock()
    private val model = Models.authUser
    private val throwable = Throwable()

    @Before
    fun setUp() {
        dataSource = AuthCacheDataSourceImpl(mockAuthCache, mockAuthMemory)
    }

    @Test
    fun `get authorized user memory success`() {
        // given
        whenever(mockAuthMemory.load(key)).thenReturn(model)
        // when
        val test = dataSource.get().test()
        // then
        verify(mockAuthMemory).load(key)
        verifyZeroInteractions(mockAuthCache)
        test.assertValue(model)
    }

    @Test
    fun `get authorized user memory fail`() {
        // given
        whenever(mockAuthMemory.load(key)).thenThrow(NoSuchElementException())
        whenever(mockAuthCache.load(key)).thenReturn(Single.error(throwable))
        // when
        val test = dataSource.get().test()
        // then
        verify(mockAuthMemory).load(key)
        verify(mockAuthCache).load(key)
        test.assertError(throwable)
    }

    @Test
    fun `get authorized user cache success`() {
        // given
        whenever(mockAuthMemory.load(key)).thenThrow(NoSuchElementException())
        whenever(mockAuthCache.load(key)).thenReturn(Single.just(model))
        // when
        val test = dataSource.get().test()
        // then
        verify(mockAuthMemory).load(key)
        verify(mockAuthCache).load(key)
        test.assertValue(model)
    }

    @Test
    fun `get authorized user cache fail`() {
        // given
        whenever(mockAuthMemory.load(key)).thenThrow(NoSuchElementException())
        whenever(mockAuthCache.load(key)).thenReturn(Single.error(throwable))
        // when
        val test = dataSource.get().test()
        // then
        verify(mockAuthMemory).load(key)
        test.assertError(throwable)
    }
}
