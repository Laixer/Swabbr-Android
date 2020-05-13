package com.laixer.swabbr.datasource.remote

import com.laixer.swabbr.Entities
import com.laixer.swabbr.Models
import com.laixer.swabbr.data.datasource.remote.ReactionRemoteDataSourceImpl
import com.laixer.swabbr.datasource.model.remote.ReactionsApi
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import org.junit.Before
import org.junit.Test

class ReactionRemoteDataSourceImplTest {

    private lateinit var dataSource: ReactionRemoteDataSourceImpl
    private val mockApi: ReactionsApi = mock()

    private val vlogId = Models.vlog.id
    private val entity = Entities.reaction
    private val model = Models.reaction

    private val entityList = listOf(entity)
    private val modelList = listOf(model)

    private val throwable = Throwable()

    @Before
    fun setUp() {
        dataSource = ReactionRemoteDataSourceImpl(mockApi)
    }

    @Test
    fun `get reactions remote success`() {
        // given
        whenever(mockApi.getReactions(vlogId)).thenReturn(Single.just(entityList))
        // when
        val test = dataSource.get(vlogId).test()
        // then
        verify(mockApi).getReactions(vlogId)
        test.assertValue(modelList)
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
