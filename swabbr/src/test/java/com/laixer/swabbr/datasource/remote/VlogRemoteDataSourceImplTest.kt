package com.laixer.swabbr.datasource.remote

import com.laixer.swabbr.datasource.model.mapToDomain
import com.laixer.swabbr.vlog
import com.laixer.swabbr.vlogEntity
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import org.junit.Before
import org.junit.Test

class VlogRemoteDataSourceImplTest {

    private lateinit var dataSource: VlogRemoteDataSourceImpl
    private val mockApi: VlogsApi = mock()
    private val vlogId = vlog.id
    private val remoteItem = vlogEntity.copy(id = "remote")
    private val remoteList = listOf(remoteItem)
    private val throwable = Throwable()

    @Before
    fun setUp() {
        dataSource = VlogRemoteDataSourceImpl(mockApi)
    }

    @Test
    fun `get featured vlogs remote success`() {
        // given
        whenever(mockApi.getFeaturedVlogs()).thenReturn(Single.just(remoteList))
        // when
        val test = dataSource.getFeaturedVlogs().test()
        // then
        verify(mockApi).getFeaturedVlogs()
        test.assertValue(remoteList.mapToDomain())
    }

    @Test
    fun `get featured vlogs remote fail`() {
        // given
        whenever(mockApi.getFeaturedVlogs()).thenReturn(Single.error(throwable))
        // when
        val test = dataSource.getFeaturedVlogs().test()
        // then
        verify(mockApi).getFeaturedVlogs()
        test.assertError(throwable)
    }

    @Test
    fun `get vlog remote success`() {
        // given
        whenever(mockApi.getVlog(vlogId)).thenReturn(Single.just(remoteItem))
        // when
        val test = dataSource.get(vlogId).test()
        // then
        verify(mockApi).getVlog(vlogId)
        test.assertValue(remoteItem.mapToDomain())
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
