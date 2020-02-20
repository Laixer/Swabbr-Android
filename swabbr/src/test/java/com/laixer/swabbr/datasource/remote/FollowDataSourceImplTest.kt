@file:Suppress("IllegalIdentifier")

package com.laixer.swabbr.datasource.remote

import com.laixer.swabbr.datasource.model.mapToDomain
import com.laixer.swabbr.followStatus
import com.laixer.swabbr.followStatusEntity
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
    private val remoteItem = followStatus
    private val throwable = Throwable()

    @Before
    fun setUp() {
        dataSource = FollowDataSourceImpl(mockApi)
    }

    @Test
    fun `get followstatus remote success`() {
        // given
        whenever(mockApi.getFollowStatus(userId)).thenReturn(Single.just(followStatusEntity))
        // when
        val test = dataSource.getFollowStatus(userId).test()
        // then
        verify(mockApi).getFollowStatus(userId)
        test.assertValue(followStatusEntity.mapToDomain())
    }

    @Test
    fun `get followstatus remote fail`() {
        // given
        whenever(mockApi.getFollowStatus(userId)).thenReturn(Single.error(throwable))
        // when
        val test = dataSource.getFollowStatus(userId).test()
        // then
        verify(mockApi).getFollowStatus(userId)
        test.assertError(throwable)
    }
}
