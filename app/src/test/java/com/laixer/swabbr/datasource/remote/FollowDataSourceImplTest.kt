package com.laixer.swabbr.datasource.remote

import com.laixer.cache.ReactiveCache
import com.laixer.swabbr.Entities
import com.laixer.swabbr.Models
import com.laixer.swabbr.data.datasource.FollowCacheDataSource
import com.laixer.swabbr.data.datasource.FollowRemoteDataSource
import com.laixer.swabbr.data.datasource.cache.FollowCacheDataSourceImpl
import com.laixer.swabbr.data.datasource.remote.FollowRemoteDataSourceImpl
import com.laixer.swabbr.data.datasource.model.remote.FollowApi
import com.laixer.swabbr.domain.model.User
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import org.junit.Before
import org.junit.Test

class FollowDataSourceImplTest {

    private lateinit var remoteDataSource: FollowRemoteDataSource
    private lateinit var cacheDataSource: FollowCacheDataSource

    private val mockApi: FollowApi = mock()

    private val mockCache: ReactiveCache<List<User>> = mock()

    private val userId = Models.user.id

    private val entity = Entities.followRequest
    private val entityList = listOf(entity)
    private val model = Models.followRequest
    private val modelList = listOf(model)

    private val throwable = Throwable()

    @Before
    fun setUp() {
        remoteDataSource = FollowRemoteDataSourceImpl(mockApi)
        cacheDataSource = FollowCacheDataSourceImpl(mockCache)
    }

    @Test
    fun `get incoming followrequest remote success`() {
        // given
        whenever(mockApi.getIncomingRequests()).thenReturn(Single.just(entityList))
        // when
        val test = remoteDataSource.getIncomingRequests().test()
        // then
        verify(mockApi).getIncomingRequests()
        test.assertValue(modelList)
    }

    @Test
    fun `get incoming followrequest remote fail`() {
        // given
        whenever(mockApi.getIncomingRequests()).thenReturn(Single.error(throwable))
        // when
        val test = remoteDataSource.getIncomingRequests().test()
        // then
        verify(mockApi).getIncomingRequests()
        test.assertError(throwable)
    }
}
