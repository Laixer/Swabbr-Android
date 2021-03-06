package com.laixer.swabbr.data.repository

import com.laixer.swabbr.Models
import com.laixer.swabbr.data.datasource.ReactionCacheDataSource
import com.laixer.swabbr.data.datasource.ReactionDataSource
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import java.util.UUID

class ReactionRepositoryImplTest {

    private lateinit var repository: ReactionRepositoryImpl
    private val mockCacheDataSource: ReactionCacheDataSource = mock()
    private val mockRemoteDataSource: ReactionDataSource = mock()
    private val vlogId = Models.vlog.id
    private val cacheItem = Models.reaction.copy(id = UUID.randomUUID())
    private val remoteItem = Models.reaction.copy(id = UUID.randomUUID())
    private val cacheList = listOf(cacheItem)
    private val remoteList = listOf(remoteItem)
    private val throwable = Throwable()

    @Before
    fun setUp() {
        repository = ReactionRepositoryImpl(mockCacheDataSource, mockRemoteDataSource)
    }

    @Test
    fun `get reactions cache success`() {
        // given
        whenever(mockCacheDataSource.get(vlogId)).thenReturn(Single.just(cacheList))
        // when
        val test = repository.get(vlogId, false).test()
        // then
        verify(mockCacheDataSource).get(vlogId)
        test.assertValue(cacheList)
    }

    @Test
    fun `get reactions cache fail fallback remote succeeds`() {
        // given
        whenever(mockCacheDataSource.get(vlogId)).thenReturn(Single.error(throwable))
        whenever(mockRemoteDataSource.getForVlog(vlogId)).thenReturn(Single.just(remoteList))
        whenever(mockCacheDataSource.set(vlogId, remoteList)).thenReturn(Single.just(remoteList))
        // when
        val test = repository.get(vlogId, false).test()
        // then
        verify(mockCacheDataSource).get(vlogId)
        verify(mockRemoteDataSource).getForVlog(vlogId)
        verify(mockCacheDataSource).set(vlogId, remoteList)
        test.assertValue(remoteList)
    }

    @Test
    fun `get reactions remote success`() {
        // given
        whenever(mockRemoteDataSource.getForVlog(vlogId)).thenReturn(Single.just(remoteList))
        whenever(mockCacheDataSource.set(vlogId, remoteList)).thenReturn(Single.just(remoteList))
        // when
        val test = repository.get(vlogId, true).test()
        // then
        verify(mockRemoteDataSource).getForVlog(vlogId)
        verify(mockCacheDataSource).set(vlogId, remoteList)
        test.assertValue(remoteList)
    }

    @Test
    fun `get reactions remote fail`() {
        // given
        whenever(mockRemoteDataSource.getForVlog(vlogId)).thenReturn(Single.error(throwable))
        // when
        val test = repository.get(vlogId, true).test()
        // then
        verify(mockRemoteDataSource).getForVlog(vlogId)
        test.assertError(throwable)
    }
}
