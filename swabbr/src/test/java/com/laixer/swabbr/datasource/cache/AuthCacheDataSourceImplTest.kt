@file:Suppress("IllegalIdentifier")

package com.laixer.swabbr.datasource.cache

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.laixer.cache.ReactiveCache
import io.reactivex.Single
import org.junit.Before
import org.junit.Test

class AuthCacheDataSourceImplTest {

    private lateinit var dataSource: AuthCacheDataSourceImpl

    private val mockAuthCache: ReactiveCache<Pair<String, String>> = mock()

    private val key = "Auth user"

    private val cacheItem = Pair("token", "userId")

    private val throwable = Throwable()

    @Before
    fun setUp() {
        dataSource = AuthCacheDataSourceImpl(mockAuthCache)
    }

    @Test
    fun `get authorized user cache success`() {
        // given
        whenever(mockAuthCache.load(key)).thenReturn(Single.just(cacheItem))

        // when
        val test = dataSource.get().test()

        // then
        verify(mockAuthCache).load(key)
        test.assertValue(cacheItem)
    }

    @Test
    fun `get authorized user cache fail`() {
        // given
        whenever(mockAuthCache.load(key)).thenReturn(Single.error(throwable))

        // when
        val test = dataSource.get().test()

        // then
        verify(mockAuthCache).load(key)
        test.assertError(throwable)
    }
}
