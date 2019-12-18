@file:Suppress("IllegalIdentifier")

package com.laixer.swabbr.datasource.remote

import com.laixer.swabbr.datasource.model.mapToDomain
import com.laixer.swabbr.settingsEntity
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import org.junit.Before
import org.junit.Test

class SettingsRemoteDataSourceImplTest {

    private lateinit var dataSource: SettingsRemoteDataSourceImpl

    private val mockApi: SettingsApi = mock()

    private val throwable = Throwable()

    @Before
    fun setUp() {
        dataSource = SettingsRemoteDataSourceImpl(mockApi)
    }

    @Test
    fun `get settings remote success`() {
        // given
        whenever(mockApi.get()).thenReturn(Single.just(settingsEntity))

        // when
        val test = dataSource.get().test()

        // then
        verify(mockApi).get()
        test.assertValue(settingsEntity.mapToDomain())
    }

    @Test
    fun `get settings remote fail`() {
        // given
        whenever(mockApi.get()).thenReturn(Single.error(throwable))

        // when
        val test = dataSource.get().test()

        // then
        verify(mockApi).get()
        test.assertError(throwable)
    }
}
