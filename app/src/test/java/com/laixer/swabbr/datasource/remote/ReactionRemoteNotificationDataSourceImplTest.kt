package com.laixer.swabbr.datasource.remote

import com.laixer.swabbr.Entities
import com.laixer.swabbr.Models
import com.laixer.swabbr.data.datasource.ReactionDataSourceImpl
import com.laixer.swabbr.data.api.ReactionApi
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import org.junit.Before
import org.junit.Test

class ReactionRemoteNotificationDataSourceImplTest {

    private lateinit var dataSource: ReactionDataSourceImpl
    private val mockApi: ReactionApi = mock()

    private val vlogId = Models.vlog.id
    private val entity = Entities.reaction
    private val model = Models.reaction

    private val entityList = listOf(entity)
    private val modelList = listOf(model)

    private val throwable = Throwable()

    @Before
    fun setUp() {
        dataSource = ReactionDataSourceImpl(mockApi)
    }

    @Test
    fun `get reactions remote success`() {
        // given
        whenever(mockApi.getReaction(vlogId)).thenReturn(Single.just(entityList))
        // when
        val test = dataSource.getForVlog(vlogId).test()
        // then
        verify(mockApi).getReaction(vlogId)
        test.assertValue(modelList)
    }

    @Test
    fun `get reactions remote fail`() {
        // given
        whenever(mockApi.getReaction(vlogId)).thenReturn(Single.error(throwable))
        // when
        val test = dataSource.getForVlog(vlogId).test()
        // then
        verify(mockApi).getReaction(vlogId)
        test.assertError(throwable)
    }
}
