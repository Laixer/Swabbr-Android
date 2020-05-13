package com.laixer.swabbr.data.repository

import com.laixer.swabbr.Models
import com.laixer.swabbr.data.datasource.UserCacheDataSource
import com.laixer.swabbr.data.datasource.UserRemoteDataSource
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import java.util.UUID

class UserRepositoryImplTest {

    private lateinit var repository: UserRepositoryImpl
    private val mockCacheDataSource: UserCacheDataSource = mock()
    private val mockRemoteDataSource: UserRemoteDataSource = mock()

    private val userId = Models.user.id

    private val cacheItem = Models.user.copy(id = UUID.randomUUID())
    private val remoteItem = Models.user.copy(id = UUID.randomUUID())

    //    private val cacheList = listOf(cacheItem)
    //    private val remoteList = listOf(remoteItem)
    private val cacheThrowable = Throwable()
    private val remoteThrowable = Throwable()

    @Before
    fun setUp() {
        repository = UserRepositoryImpl(mockCacheDataSource, mockRemoteDataSource)
    }

    /**
     * These tests are currently using a removed feature (retrieving all users) because of backend limitations.
     */
//    @Test
//    fun `get users cache success`() {
//        // given
//        whenever(mockCacheDataSource.get()).thenReturn(Single.just(cacheList))
//        // when
//        val test = repository.get(false).test()
//        // then
//        verify(mockCacheDataSource).get()
//        test.assertValue(cacheList)
//    }
//
//    @Test
//    fun `get users cache fail fallback remote succeeds`() {
//        // given
//        whenever(mockCacheDataSource.get()).thenReturn(Single.error(cacheThrowable))
//        whenever(mockRemoteDataSource.get()).thenReturn(Single.just(remoteList))
//        whenever(mockCacheDataSource.set(remoteList)).thenReturn(Single.just(remoteList))
//        // when
//        val test = repository.get(false).test()
//        // then
//        verify(mockCacheDataSource).get()
//        verify(mockRemoteDataSource).get()
//        verify(mockCacheDataSource).set(remoteList)
//        test.assertValue(remoteList)
//    }
//
//    @Test
//    fun `get users cache fail fallback remote fails`() {
//        // given
//        whenever(mockCacheDataSource.get()).thenReturn(Single.error(cacheThrowable))
//        whenever(mockRemoteDataSource.get()).thenReturn(Single.error(remoteThrowable))
//        // when
//        val test = repository.get(false).test()
//        // then
//        verify(mockCacheDataSource).get()
//        verify(mockRemoteDataSource).get()
//        test.assertError(remoteThrowable)
//    }
//
//    @Test
//    fun `get users remote success`() {
//        // given
//        whenever(mockRemoteDataSource.get()).thenReturn(Single.just(remoteList))
//        whenever(mockCacheDataSource.set(remoteList)).thenReturn(Single.just(remoteList))
//        // when
//        val test = repository.get(true).test()
//        // then
//        verify(mockRemoteDataSource).get()
//        verify(mockCacheDataSource).set(remoteList)
//        test.assertValue(remoteList)
//    }
//
//    @Test
//    fun `get users remote fail`() {
//        // given
//        whenever(mockRemoteDataSource.get()).thenReturn(Single.error(remoteThrowable))
//        // when
//        val test = repository.get(true).test()
//        // then
//        verify(mockRemoteDataSource).get()
//        test.assertError(remoteThrowable)
//    }
//
    @Test
    fun `get user cache success`() {
        // given
        whenever(mockCacheDataSource.get(userId)).thenReturn(Single.just(cacheItem))
        // when
        val test = repository.get(userId, refresh = false).test()
        // then
        verify(mockCacheDataSource).get(userId)
        test.assertValue(cacheItem)
    }

    @Test
    fun `get user cache fail fallback remote succeeds`() {
        // given
        whenever(mockCacheDataSource.get(userId)).thenReturn(Single.error(cacheThrowable))
        whenever(mockRemoteDataSource.get(userId)).thenReturn(Single.just(remoteItem))
        whenever(mockCacheDataSource.set(remoteItem)).thenReturn(Single.just(remoteItem))
        // when
        val test = repository.get(userId, refresh = false).test()
        // then
        verify(mockCacheDataSource).get(userId)
        verify(mockRemoteDataSource).get(userId)
        verify(mockCacheDataSource).set(remoteItem)
        test.assertValue(remoteItem)
    }

    @Test
    fun `get user cache fail fallback remote fails`() {
        // given
        whenever(mockCacheDataSource.get(userId)).thenReturn(Single.error(cacheThrowable))
        whenever(mockRemoteDataSource.get(userId)).thenReturn(Single.error(remoteThrowable))
        // when
        val test = repository.get(userId, refresh = false).test()
        // then
        verify(mockCacheDataSource).get(userId)
        verify(mockRemoteDataSource).get(userId)
        test.assertError(remoteThrowable)
    }

    @Test
    fun `get user remote success`() {
        // given
        whenever(mockRemoteDataSource.get(userId)).thenReturn(Single.just(remoteItem))
        whenever(mockCacheDataSource.set(remoteItem)).thenReturn(Single.just(remoteItem))
        // when
        val test = repository.get(userId, refresh = true).test()
        // then
        verify(mockRemoteDataSource).get(userId)
        verify(mockCacheDataSource).set(remoteItem)
        test.assertValue(remoteItem)
    }

    @Test
    fun `get user remote fail`() {
        // given
        whenever(mockRemoteDataSource.get(userId)).thenReturn(Single.error(remoteThrowable))
        // when
        val test = repository.get(userId, true).test()
        // then
        verify(mockRemoteDataSource).get(userId)
        test.assertError(remoteThrowable)
    }


}
