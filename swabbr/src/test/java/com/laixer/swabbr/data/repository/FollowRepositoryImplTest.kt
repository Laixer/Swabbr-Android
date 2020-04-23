package com.laixer.swabbr.data.repository

import com.laixer.swabbr.Items
import com.laixer.swabbr.Models
import com.laixer.swabbr.data.datasource.FollowDataSource
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import org.junit.Before
import org.junit.Test

class FollowRepositoryImplTest {

    private lateinit var repository: FollowRepositoryImpl
    private val mockRemoteDataSource: FollowDataSource = mock()
    private val userId = Models.user.id

    private val followRequest = Models.followRequest

    private val userModel = Models.user
    private val userModelList = listOf(userModel)
    private val userItem = Items.user
    private val userItemList = listOf(userItem)

    private val throwable = Throwable()

    @Before
    fun setUp() {
        repository = FollowRepositoryImpl(mockRemoteDataSource)
    }

    @Test
    fun `get follow status success`() {
        // given
        whenever(mockRemoteDataSource.getFollowStatus(userId)).thenReturn(Single.just(followRequest))
        // when
        val test = repository.getFollowStatus(userId).test()
        // then
        verify(mockRemoteDataSource).getFollowStatus(userId)
        test.assertValue(followRequest)
    }

    @Test
    fun `get follow status fail`() {
        // given
        whenever(mockRemoteDataSource.getFollowStatus(userId)).thenReturn(Single.error(throwable))
        // when
        val test = repository.getFollowStatus(userId).test()
        // then
        verify(mockRemoteDataSource).getFollowStatus(userId)
        test.assertError(throwable)
    }

    @Test
    fun `get followers success`() {
        // given
        whenever(mockRemoteDataSource.getFollowers(userId)).thenReturn(Single.just(userModelList))
        // when
        val test = repository.getFollowers(userId).test()
        // then
        verify(mockRemoteDataSource).getFollowers(userId)
        test.assertValue(userModelList)
    }

    @Test
    fun `get followers fail`() {
        // given
        whenever(mockRemoteDataSource.getFollowStatus(userId)).thenReturn(Single.error(throwable))
        // when
        val test = repository.getFollowStatus(userId).test()
        // then
        verify(mockRemoteDataSource).getFollowStatus(userId)
        test.assertError(throwable)
    }

    @Test
    fun `get following success`() {
        // given
        whenever(mockRemoteDataSource.getFollowing(userId)).thenReturn(Single.just(userModelList))
        // when
        val test = repository.getFollowing(userId).test()
        // then
        verify(mockRemoteDataSource).getFollowing(userId)
        test.assertValue(userModelList)
    }

    @Test
    fun `get following fail`() {
        // given
        whenever(mockRemoteDataSource.getFollowing(userId)).thenReturn(Single.error(throwable))
        // when
        val test = repository.getFollowing(userId).test()
        // then
        verify(mockRemoteDataSource).getFollowing(userId)
        test.assertError(throwable)
    }

    @Test
    fun `get incoming requests success`() {
        // given
        whenever(mockRemoteDataSource.getIncomingRequests()).thenReturn(Single.just(userModelList))
        // when
        val test = repository.getIncomingRequests().test()
        // then
        verify(mockRemoteDataSource).getIncomingRequests()
        test.assertValue(userModelList)
    }

    @Test
    fun `get incoming requests fail`() {
        // given
        whenever(mockRemoteDataSource.getIncomingRequests()).thenReturn(Single.error(throwable))
        // when
        val test = repository.getIncomingRequests().test()
        // then
        verify(mockRemoteDataSource).getIncomingRequests()
        test.assertError(throwable)
    }

    @Test
    fun `send follow request success`() {
        // given
        whenever(mockRemoteDataSource.sendFollowRequest(userId)).thenReturn(Single.just(followRequest))
        // when
        val test = repository.sendFollowRequest(userId).test()
        // then
        verify(mockRemoteDataSource).sendFollowRequest(userId)
        test.assertValue(followRequest)
    }

    @Test
    fun `send follow request fail`() {
        // given
        whenever(mockRemoteDataSource.sendFollowRequest(userId)).thenReturn(Single.error(throwable))
        // when
        val test = repository.sendFollowRequest(userId).test()
        // then
        verify(mockRemoteDataSource).sendFollowRequest(userId)
        test.assertError(throwable)
    }

    @Test
    fun `cancel follow request success`() {
        // given
        whenever(mockRemoteDataSource.cancelFollowRequest(userId)).thenReturn(Single.just(followRequest))
        // when
        val test = repository.cancelFollowRequest(userId).test()
        // then
        verify(mockRemoteDataSource).cancelFollowRequest(userId)
        test.assertValue(followRequest)
    }

    @Test
    fun `cancel follow request fail`() {
        // given
        whenever(mockRemoteDataSource.cancelFollowRequest(userId)).thenReturn(Single.error(throwable))
        // when
        val test = repository.cancelFollowRequest(userId).test()
        // then
        verify(mockRemoteDataSource).cancelFollowRequest(userId)
        test.assertError(throwable)
    }

    @Test
    fun `unfollow success`() {
        // given
        whenever(mockRemoteDataSource.unfollow(userId)).thenReturn(Single.just(followRequest))
        // when
        val test = repository.unfollow(userId).test()
        // then
        verify(mockRemoteDataSource).unfollow(userId)
        test.assertValue(followRequest)
    }

    @Test
    fun `unfollow fail`() {
        // given
        whenever(mockRemoteDataSource.unfollow(userId)).thenReturn(Single.error(throwable))
        // when
        val test = repository.unfollow(userId).test()
        // then
        verify(mockRemoteDataSource).unfollow(userId)
        test.assertError(throwable)
    }

    @Test
    fun `accept request success`() {
        // given
        whenever(mockRemoteDataSource.acceptRequest(userId)).thenReturn(Single.just(followRequest))
        // when
        val test = repository.acceptRequest(userId).test()
        // then
        verify(mockRemoteDataSource).acceptRequest(userId)
        test.assertValue(followRequest)
    }

    @Test
    fun `accept request fail`() {
        // given
        whenever(mockRemoteDataSource.acceptRequest(userId)).thenReturn(Single.error(throwable))
        // when
        val test = repository.acceptRequest(userId).test()
        // then
        verify(mockRemoteDataSource).acceptRequest(userId)
        test.assertError(throwable)
    }

    @Test
    fun `decline request success`() {
        // given
        whenever(mockRemoteDataSource.declineRequest(userId)).thenReturn(Single.just(followRequest))
        // when
        val test = repository.declineRequest(userId).test()
        // then
        verify(mockRemoteDataSource).declineRequest(userId)
        test.assertValue(followRequest)
    }

    @Test
    fun `decline request fail`() {
        // given
        whenever(mockRemoteDataSource.declineRequest(userId)).thenReturn(Single.error(throwable))
        // when
        val test = repository.declineRequest(userId).test()
        // then
        verify(mockRemoteDataSource).declineRequest(userId)
        test.assertError(throwable)
    }
}
