@file:Suppress("IllegalIdentifier")

package com.laixer.swabbr.data.repository

import com.laixer.swabbr.data.datasource.FollowDataSource
import com.laixer.swabbr.followStatus
import com.laixer.swabbr.user
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import org.junit.Before
import org.junit.Test

class FollowRepositoryImplTest {

    private lateinit var repository: FollowRepositoryImpl
    private val mockRemoteDataSource: FollowDataSource = mock()
    private val userId = user.id
    private val remoteItem = followStatus
    private val remoteThrowable = Throwable()

    @Before
    fun setUp() {
        repository = FollowRepositoryImpl(mockRemoteDataSource)
    }

    @Test
    fun `get follow remote success`() {
        // given
        whenever(mockRemoteDataSource.getFollowStatus(userId)).thenReturn(Single.just(remoteItem))
        // when
        val test = repository.getFollowStatus(userId).test()
        // then
        verify(mockRemoteDataSource).getFollowStatus(userId)
        test.assertValue(remoteItem)
    }

    @Test
    fun `get follow remote fail`() {
        // given
        whenever(mockRemoteDataSource.getFollowStatus(userId)).thenReturn(Single.error(remoteThrowable))
        // when
        val test = repository.getFollowStatus(userId).test()
        // then
        verify(mockRemoteDataSource).getFollowStatus(userId)
        test.assertError(remoteThrowable)
    }
}
