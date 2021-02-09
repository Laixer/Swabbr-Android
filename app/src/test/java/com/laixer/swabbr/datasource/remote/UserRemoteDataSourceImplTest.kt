package com.laixer.swabbr.datasource.remote

import com.laixer.swabbr.Entities
import com.laixer.swabbr.Models
import com.laixer.swabbr.data.datasource.UserDataSourceImpl
import com.laixer.swabbr.data.api.UserApi
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import org.junit.Before
import org.junit.Test

class UserRemoteDataSourceImplTest {

    private lateinit var dataSource: UserDataSourceImpl
    private val mockApi: UserApi = mock()
    private val userId = Models.user.id

    private val entity = Entities.user
    private val model = Models.user

    private val entityList = listOf(entity)
    private val modelList = listOf(model)

    private val throwable = Throwable()

    @Before
    fun setUp() {
        dataSource = UserDataSourceImpl(mockApi)
    }

    @Test
    fun `search users remote success`() {
        // given
        whenever(mockApi.search(model.firstName)).thenReturn(Single.just(entityList))
        // when
        val test = dataSource.search(model.firstName).test()
        // then
        verify(mockApi).search(model.firstName)
        test.assertValue(modelList)
    }

    @Test
    fun `search users remote fail`() {
        // given
        whenever(mockApi.search(model.firstName)).thenReturn(Single.error(throwable))
        // when
        val test = dataSource.search(model.firstName).test()
        // then
        verify(mockApi).search(model.firstName)
        test.assertError(throwable)
    }

    @Test
    fun `get user remote success`() {
        // given
        whenever(mockApi.getUser(userId)).thenReturn(Single.just(entity))
        // when
        val test = dataSource.get(userId).test()
        // then
        verify(mockApi).getUser(userId)
        test.assertValue(model)
    }

    @Test
    fun `get user remote fail`() {
        // given
        whenever(mockApi.getUser(userId)).thenReturn(Single.error(throwable))
        // when
        val test = dataSource.get(userId).test()
        // then
        verify(mockApi).getUser(userId)
        test.assertError(throwable)
    }
}
