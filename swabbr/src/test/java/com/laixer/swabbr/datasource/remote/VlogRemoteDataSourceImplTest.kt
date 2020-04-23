package com.laixer.swabbr.datasource.remote

import com.laixer.swabbr.Entities
import com.laixer.swabbr.Models
import com.laixer.swabbr.data.datasource.remote.VlogRemoteDataSourceImpl
import com.laixer.swabbr.datasource.model.remote.VlogsApi
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import org.junit.Before
import org.junit.Test

class VlogRemoteDataSourceImplTest {

    private lateinit var dataSource: VlogRemoteDataSourceImpl
    private val mockApi: VlogsApi = mock()

    private val vlogId = Models.vlog.id

    private val entity = Entities.vlog
    private val model = Models.vlog

    private val entityList = listOf(entity)
    private val modelList = listOf(model)

    private val throwable = Throwable()

    @Before
    fun setUp() {
        dataSource = VlogRemoteDataSourceImpl(mockApi)
    }

    @Test
    fun `get recommended vlogs remote success`() {
        // given
        whenever(mockApi.getRecommendedVlogs()).thenReturn(Single.just(entityList))
        // when
        val test = dataSource.getRecommendedVlogs().test()
        // then
        verify(mockApi).getRecommendedVlogs()
        test.assertValue(modelList)
    }

    @Test
    fun `get recommended vlogs remote fail`() {
        // given
        whenever(mockApi.getRecommendedVlogs()).thenReturn(Single.error(throwable))
        // when
        val test = dataSource.getRecommendedVlogs().test()
        // then
        verify(mockApi).getRecommendedVlogs()
        test.assertError(throwable)
    }

    @Test
    fun `get vlog remote success`() {
        // given
        whenever(mockApi.getVlog(vlogId)).thenReturn(Single.just(entity))
        // when
        val test = dataSource.get(vlogId).test()
        // then
        verify(mockApi).getVlog(vlogId)
        test.assertValue(model)
    }

    @Test
    fun `get vlog remote fail`() {
        // given
        whenever(mockApi.getVlog(vlogId)).thenReturn(Single.error(throwable))
        // when
        val test = dataSource.get(vlogId).test()
        // then
        verify(mockApi).getVlog(vlogId)
        test.assertError(throwable)
    }
}
