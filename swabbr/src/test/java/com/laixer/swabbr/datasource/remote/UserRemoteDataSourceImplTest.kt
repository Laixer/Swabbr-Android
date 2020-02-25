package com.laixer.swabbr.datasource.remote

import com.laixer.swabbr.datasource.model.mapToDomain
import com.laixer.swabbr.user
import com.laixer.swabbr.userEntity
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import org.junit.Before
import org.junit.Test

class UserRemoteDataSourceImplTest {

    private lateinit var dataSource: UserRemoteDataSourceImpl
    private val mockApi: UsersApi = mock()
    private val userId = user.id
    private val remoteItem = userEntity.copy(id = "remote")
    private val remoteList = listOf(remoteItem)
    private val throwable = Throwable()

    @Before
    fun setUp() {
        dataSource = UserRemoteDataSourceImpl(mockApi)
    }

    @Test
    fun `search users remote success`() {
        // given
        whenever(mockApi.searchUserByFirstname(user.firstName)).thenReturn(Single.just(remoteList))
        // when
        val test = dataSource.search(user.firstName).test()
        // then
        verify(mockApi).searchUserByFirstname(user.firstName)
        test.assertValue(remoteList.mapToDomain())
    }

    @Test
    fun `search users remote fail`() {
        // given
        whenever(mockApi.searchUserByFirstname(user.firstName)).thenReturn(Single.error(throwable))
        // when
        val test = dataSource.search(user.firstName).test()
        // then
        verify(mockApi).searchUserByFirstname(user.firstName)
        test.assertError(throwable)
    }

    @Test
    fun `get user remote success`() {
        // given
        whenever(mockApi.getUser(userId)).thenReturn(Single.just(remoteItem))
        // when
        val test = dataSource.get(userId).test()
        // then
        verify(mockApi).getUser(userId)
        test.assertValue(remoteItem.mapToDomain())
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
