package com.laixer.swabbr.datasource.remote

import com.laixer.cache.ReactiveCache
import com.laixer.swabbr.Entities
import com.laixer.swabbr.Models
import com.laixer.swabbr.data.datasource.FollowRequestCacheDataSource
import com.laixer.swabbr.data.datasource.FollowRequestDataSource
import com.laixer.swabbr.data.cache.FollowRequestCacheDataSourceImpl
import com.laixer.swabbr.data.datasource.FollowRequestRemoteDataSourceImpl
import com.laixer.swabbr.data.api.FollowRequestApi
import com.laixer.swabbr.domain.model.User
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import org.junit.Before
import org.junit.Test

class FollowDataSourceImplTest {

    private lateinit var requestDataSource: FollowRequestDataSource
    private lateinit var requestCacheDataSource: FollowRequestCacheDataSource

    private val mockRequestApi: FollowRequestApi = mock()

    private val mockCache: ReactiveCache<List<User>> = mock()

    private val userId = Models.user.id

    private val entity = Entities.followRequest
    private val entityList = listOf(entity)
    private val model = Models.followRequest
    private val modelList = listOf(model)

    private val throwable = Throwable()

    @Before
    fun setUp() {
        requestDataSource = FollowRequestRemoteDataSourceImpl(mockRequestApi)
        requestCacheDataSource = FollowRequestCacheDataSourceImpl(mockCache)
    }

    @Test
    fun `get incoming followrequest remote success`() {
        // given
        whenever(mockRequestApi.getIncomingRequests()).thenReturn(Single.just(entityList))
        // when
        val test = requestDataSource.getIncomingRequests().test()
        // then
        verify(mockRequestApi).getIncomingRequests()
        test.assertValue(modelList)
    }

    @Test
    fun `get incoming followrequest remote fail`() {
        // given
        whenever(mockRequestApi.getIncomingRequests()).thenReturn(Single.error(throwable))
        // when
        val test = requestDataSource.getIncomingRequests().test()
        // then
        verify(mockRequestApi).getIncomingRequests()
        test.assertError(throwable)
    }
}
