package com.laixer.swabbr.datasource.remote

import com.laixer.swabbr.Entities
import com.laixer.swabbr.Models
import com.laixer.swabbr.data.datasource.remote.FollowDataSourceImpl
import com.laixer.swabbr.datasource.model.remote.FollowApi
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import org.junit.Before
import org.junit.Test

class FollowDataSourceImplTest {

    private lateinit var dataSource: FollowDataSourceImpl
    private val mockApi: FollowApi = mock()

    private val userId = Models.user.id

    private val entity = Entities.followRequest
    private val model = Models.followRequest

    private val throwable = Throwable()

    @Before
    fun setUp() {
        dataSource = FollowDataSourceImpl(mockApi)
    }

    @Test
    fun `get followrequest remote success`() {
        // given
        whenever(mockApi.getFollowRequest(userId)).thenReturn(Single.just(entity))
        // when
        val test = dataSource.getFollowStatus(userId).test()
        // then
        verify(mockApi).getFollowRequest(userId)
        test.assertValue(model)
    }

    @Test
    fun `get followrequest remote fail`() {
        // given
        whenever(mockApi.getFollowRequest(userId)).thenReturn(Single.error(throwable))
        // when
        val test = dataSource.getFollowStatus(userId).test()
        // then
        verify(mockApi).getFollowRequest(userId)
        test.assertError(throwable)
    }
}
