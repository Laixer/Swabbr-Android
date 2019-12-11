@file:Suppress("IllegalIdentifier")

package com.laixer.swabbr.datasource.cache

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.laixer.cache.ReactiveCache
import com.laixer.swabbr.domain.model.Settings
import com.laixer.swabbr.settings
import io.reactivex.Single
import org.junit.Before
import org.junit.Test

class SettingsCacheDataSourceImplTest {

    private lateinit var dataSource: SettingsCacheDataSourceImpl

    private val mockCache: ReactiveCache<Settings> = mock()

    val key = "Settings"

    private val cacheItem = settings.copy(true, 0, 0)

    private val throwable = Throwable()

    @Before
    fun setUp() {
        dataSource = SettingsCacheDataSourceImpl(mockCache)
    }

    @Test
    fun `get settings cache success`() {
        // given
        whenever(mockCache.load(key)).thenReturn(Single.just(cacheItem))

        // when
        val test = dataSource.get().test()

        // then
        verify(mockCache).load(key)
        test.assertValue(cacheItem)
    }

    @Test
    fun `get settings cache fail`() {
        // given
        whenever(mockCache.load(key)).thenReturn(Single.error(throwable))

        // when
        val test = dataSource.get().test()

        // then
        verify(mockCache).load(key)
        test.assertError(throwable)
    }
}
