package com.laixer.swabbr.datasource.remote

import com.laixer.swabbr.Entities
import com.laixer.swabbr.Models
import com.laixer.swabbr.data.api.SettingsApi
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import org.junit.Before
import org.junit.Test

class SettingsRemoteNotificationDataSourceImplTest {

    private lateinit var dataSource: SettingsRemoteDataSourceImpl
    private val mockApi: SettingsApi = mock()
    private val throwable = Throwable()

    private val entity = Entities.settings
    private val model = Models.settings

    @Before
    fun setUp() {
        dataSource = SettingsRemoteDataSourceImpl(mockApi)
    }

    @Test
    fun `get settings remote success`() {
        // given
        whenever(mockApi.get()).thenReturn(Single.just(entity))
        // when
        val test = dataSource.get().test()
        // then
        verify(mockApi).get()
        test.assertValue(model)
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
