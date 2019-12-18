@file:Suppress("IllegalIdentifier")

package com.laixer.swabbr.datasource.remote

import com.laixer.swabbr.datasource.model.mapToDomain
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.laixer.swabbr.reactionEntity
import com.laixer.swabbr.vlog
import io.reactivex.Single
import org.junit.Before
import org.junit.Test

class ReactionRemoteDataSourceImplTest {

    private lateinit var dataSource: ReactionRemoteDataSourceImpl

    private val mockApi: ReactionsApi = mock()

    private val vlogId = vlog.id

    private val remoteItem = reactionEntity.copy(id = "remote")

    private val remoteList = listOf(remoteItem)

    private val throwable = Throwable()

    @Before
    fun setUp() {
        dataSource = ReactionRemoteDataSourceImpl(mockApi)
    }

    @Test
    fun `get reactions remote success`() {
        // given
        whenever(mockApi.getReactions(vlogId)).thenReturn(Single.just(remoteList))

        // when
        val test = dataSource.get(vlogId).test()

        // then
        verify(mockApi).getReactions(vlogId)
        test.assertValue(remoteList.mapToDomain())
    }

    @Test
    fun `get reactions remote fail`() {
        // given
        whenever(mockApi.getReactions(vlogId)).thenReturn(Single.error(throwable))

        // when
        val test = dataSource.get(vlogId).test()

        // then
        verify(mockApi).getReactions(vlogId)
        test.assertError(throwable)
    }
}
