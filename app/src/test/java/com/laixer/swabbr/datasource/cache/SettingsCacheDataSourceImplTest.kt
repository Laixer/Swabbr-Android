package com.laixer.swabbr.datasource.cache

import com.laixer.cache.ReactiveCache
import com.laixer.swabbr.Models
import com.laixer.swabbr.data.datasource.cache.SettingsCacheDataSourceImpl
import com.laixer.swabbr.domain.model.Settings
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import org.junit.Before
import org.junit.Test

class SettingsCacheDataSourceImplTest {

    private lateinit var dataSource: SettingsCacheDataSourceImpl
    private val key by lazy { dataSource.key }

    private val mockCache: ReactiveCache<Settings> = mock()

    private val model = Models.settings
    private val throwable = Throwable()

    @Before
    fun setUp() {
        dataSource = SettingsCacheDataSourceImpl(mockCache)
    }

    @Test
    fun `get settings cache success`() {
        // given
        whenever(mockCache.load(key)).thenReturn(Single.just(model))
        // when
        val test = dataSource.get().test()
        // then
        verify(mockCache).load(key)
        test.assertValue(model)
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
