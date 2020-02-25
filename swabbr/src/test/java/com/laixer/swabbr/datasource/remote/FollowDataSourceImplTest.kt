package com.laixer.swabbr.datasource.remote

import com.laixer.swabbr.datasource.model.mapToDomain
import com.laixer.swabbr.followRequestEntity
import com.laixer.swabbr.user
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import org.junit.Before
import org.junit.Test

class FollowDataSourceImplTest {

    private lateinit var dataSource: FollowDataSourceImpl
    private val mockApi: FollowApi = mock()
    private val userId = user.id
    private val throwable = Throwable()

    @Before
    fun setUp() {
        dataSource = FollowDataSourceImpl(mockApi)
    }

    @Test
    fun `get followrequest remote success`() {
        // given
        whenever(mockApi.getFollowRequest(userId)).thenReturn(Single.just(followRequestEntity))
        // when
        val test = dataSource.getFollowStatus(userId).test()
        // then
        verify(mockApi).getFollowRequest(userId)
        test.assertValue(followRequestEntity.mapToDomain())
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
