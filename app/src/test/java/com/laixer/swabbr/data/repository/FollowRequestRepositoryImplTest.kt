package com.laixer.swabbr.data.repository

import com.laixer.swabbr.Items
import com.laixer.swabbr.Models
import com.laixer.swabbr.data.datasource.FollowRequestCacheDataSource
import com.laixer.swabbr.data.datasource.FollowRequestDataSource
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.Before
import org.junit.Test

class FollowRequestRepositoryImplTest {

    private lateinit var repository: FollowRequestRepositoryImpl
    private val mockRequestDataSource: FollowRequestDataSource = mock()
    private val mockRequestCacheDataSource: FollowRequestCacheDataSource = mock()

    private val userId = Models.user.id

    private val followRequest = Models.followRequest
    private val followRequestList = listOf(followRequest)

    private val followStatus = Models.followStatus

    private val userModel = Models.user
    private val userModelList = listOf(userModel)
    private val userItem = Items.user
    private val userItemList = listOf(userItem)

    private val throwable = Throwable()

    @Before
    fun setUp() {
        repository = FollowRequestRepositoryImpl(mockRequestDataSource, mockRequestCacheDataSource)
    }

    @Test
    fun `get follow status success`() {
        // given
        whenever(mockRequestDataSource.getFollowStatus(userId)).thenReturn(Single.just(followStatus))
        // when
        val test = repository.getFollowStatus(userId).test()
        // then
        verify(mockRequestDataSource).getFollowStatus(userId)
        test.assertValue(followStatus)
    }

    @Test
    fun `get follow status fail`() {
        // given
        whenever(mockRequestDataSource.getFollowStatus(userId)).thenReturn(Single.error(throwable))
        // when
        val test = repository.getFollowStatus(userId).test()
        // then
        verify(mockRequestDataSource).getFollowStatus(userId)
        test.assertError(throwable)
    }

    @Test
    fun `get incoming requests success`() {
        // given
        whenever(mockRequestDataSource.getIncomingRequests()).thenReturn(Single.just(followRequestList))
        // when
        val test = repository.getIncomingRequests().test()
        // then
        verify(mockRequestDataSource).getIncomingRequests()
        test.assertValue(followRequestList)
    }

    @Test
    fun `get incoming requests fail`() {
        // given
        whenever(mockRequestDataSource.getIncomingRequests()).thenReturn(Single.error(throwable))
        // when
        val test = repository.getIncomingRequests().test()
        // then
        verify(mockRequestDataSource).getIncomingRequests()
        test.assertError(throwable)
    }

    @Test
    fun `send follow request success`() {
        // given
        whenever(mockRequestDataSource.sendFollowRequest(userId)).thenReturn(Single.just(followRequest))
        // when
        val test = repository.sendFollowRequest(userId).test()
        // then
        verify(mockRequestDataSource).sendFollowRequest(userId)
        test.assertValue(followRequest)
    }

    @Test
    fun `send follow request fail`() {
        // given
        whenever(mockRequestDataSource.sendFollowRequest(userId)).thenReturn(Single.error(throwable))
        // when
        val test = repository.sendFollowRequest(userId).test()
        // then
        verify(mockRequestDataSource).sendFollowRequest(userId)
        test.assertError(throwable)
    }

    @Test
    fun `cancel follow request success`() {
        // given
        whenever(mockRequestDataSource.cancelFollowRequest(userId)).thenReturn(Completable.complete())
        // when
        val test = repository.cancelFollowRequest(userId).test()
        // then
        verify(mockRequestDataSource).cancelFollowRequest(userId)
        test.assertNoErrors()
    }

    @Test
    fun `cancel follow request fail`() {
        // given
        whenever(mockRequestDataSource.cancelFollowRequest(userId)).thenReturn(Completable.error(throwable))
        // when
        val test = repository.cancelFollowRequest(userId).test()
        // then
        verify(mockRequestDataSource).cancelFollowRequest(userId)
        test.assertError(throwable)
    }

    @Test
    fun `unfollow success`() {
        // given
        whenever(mockRequestDataSource.unfollow(userId)).thenReturn(Completable.complete())
        // when
        val test = repository.unfollow(userId).test()
        // then
        verify(mockRequestDataSource).unfollow(userId)
        test.assertNoErrors()
    }

    @Test
    fun `unfollow fail`() {
        // given
        whenever(mockRequestDataSource.unfollow(userId)).thenReturn(Completable.error(throwable))
        // when
        val test = repository.unfollow(userId).test()
        // then
        verify(mockRequestDataSource).unfollow(userId)
        test.assertError(throwable)
    }

    @Test
    fun `accept request success`() {
        // given
        whenever(mockRequestDataSource.acceptRequest(userId)).thenReturn(Single.just(followRequest))
        // when
        val test = repository.acceptRequest(userId).test()
        // then
        verify(mockRequestDataSource).acceptRequest(userId)
        test.assertValue(followRequest)
    }

    @Test
    fun `accept request fail`() {
        // given
        whenever(mockRequestDataSource.acceptRequest(userId)).thenReturn(Single.error(throwable))
        // when
        val test = repository.acceptRequest(userId).test()
        // then
        verify(mockRequestDataSource).acceptRequest(userId)
        test.assertError(throwable)
    }

    @Test
    fun `decline request success`() {
        // given
        whenever(mockRequestDataSource.declineRequest(userId)).thenReturn(Single.just(followRequest))
        // when
        val test = repository.declineRequest(userId).test()
        // then
        verify(mockRequestDataSource).declineRequest(userId)
        test.assertValue(followRequest)
    }

    @Test
    fun `decline request fail`() {
        // given
        whenever(mockRequestDataSource.declineRequest(userId)).thenReturn(Single.error(throwable))
        // when
        val test = repository.declineRequest(userId).test()
        // then
        verify(mockRequestDataSource).declineRequest(userId)
        test.assertError(throwable)
    }

    @Test
    fun `get followers success`() {
        // given
        whenever(mockRequestDataSource.getFollowers(userId)).thenReturn(Single.just(userModelList))
        // when
        val test = repository.getFollowers(userId).test()
        // then
        verify(mockRequestDataSource).getFollowers(userId)
        test.assertValue(userModelList)
    }

    @Test
    fun `get followers fail`() {
        // given
        whenever(mockRequestDataSource.getFollowStatus(userId)).thenReturn(Single.error(throwable))
        // when
        val test = repository.getFollowStatus(userId).test()
        // then
        verify(mockRequestDataSource).getFollowStatus(userId)
        test.assertError(throwable)
    }

    @Test
    fun `get following success`() {
        // given
        whenever(mockRequestDataSource.getFollowing(userId)).thenReturn(Single.just(userModelList))
        // when
        val test = repository.getFollowing(userId).test()
        // then
        verify(mockRequestDataSource).getFollowing(userId)
        test.assertValue(userModelList)
    }

    @Test
    fun `get following fail`() {
        // given
        whenever(mockRequestDataSource.getFollowing(userId)).thenReturn(Single.error(throwable))
        // when
        val test = repository.getFollowing(userId).test()
        // then
        verify(mockRequestDataSource).getFollowing(userId)
        test.assertError(throwable)
    }
}
