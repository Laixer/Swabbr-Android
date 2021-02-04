package com.laixer.swabbr.presentation.vlogs.playback

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.swabbr.Items
import com.laixer.swabbr.Models
import com.laixer.swabbr.domain.usecase.ReactionUseCase
import com.laixer.swabbr.domain.usecase.VlogUseCase
import com.laixer.swabbr.presentation.RxSchedulersOverrideRule
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import java.util.UUID

class VlogViewModelTest {

    private lateinit var viewModel: VlogViewModel
    private val mockVlogUseCase: VlogUseCase = mock()
    private val mockReactionUseCase: ReactionUseCase = mock()

    private val combinedUserReactionModel = Pair(Models.user, Models.reaction)
    private val combindUserReactionsModelList = listOf(combinedUserReactionModel)

    private val userModel = Models.user
    private val vlogModel = Models.vlog

    private val userVlogModel = Pair(userModel, vlogModel)
    private val userVlogModelList = listOf(userVlogModel)
    private val userVlogItem = Items.uservlog
    private val userVlogItemList = listOf(userVlogItem)

    private val reactionItem = Items.reaction
    private val reactionItemList = listOf(reactionItem)

    private val userId = Models.user.id
    private val vlogId = Models.vlog.id

    private val throwable = Throwable()

    @Rule
    @JvmField
    val rxSchedulersOverrideRule = RxSchedulersOverrideRule()

    @Rule
    @JvmField
    val instantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        viewModel = VlogViewModel(mockVlogUseCase, mockReactionUseCase)
    }

    @Test
    fun `get single vlog succeeds`() {
        // given
        val idList = listOf(vlogId)
        whenever(mockVlogUseCase.getFromIdList(idList, false))
            .thenReturn(Single.just(userVlogModelList))
        // when
        viewModel.getVlogsForUser(idList)
        // then
        verify(mockVlogUseCase).getFromIdList(idList, false)
        assertEquals(userVlogItem, viewModel.vlogs.value?.data?.get(0))
    }

    @Test
    fun `get multiple vlogs succeeds`() {
        // given
        val idList = listOf(vlogId, vlogId)
        whenever(mockVlogUseCase.getFromIdList(idList, false))
            .thenReturn(Single.just(listOf(userVlogModel, userVlogModel)))
        // when
        viewModel.getVlogsForUser(idList)
        // then
        verify(mockVlogUseCase).getFromIdList(idList, false)
        assertEquals(userVlogItem, viewModel.vlogs.value?.data?.get(0))
        assertEquals(userVlogItem, viewModel.vlogs.value?.data?.get(1))
    }

    @Test
    fun `get zero vlogs succeeds`() {
        // given
        val idList = listOf<UUID>()
        whenever(mockVlogUseCase.getFromIdList(idList, false))
            .thenReturn(Single.just(listOf()))
        // when
        viewModel.getVlogsForUser(idList)
        // then
        verify(mockVlogUseCase).getFromIdList(idList, false)
        assert(viewModel.vlogs.value?.data?.isEmpty() ?: false)
    }

    @Test
    fun `get reactions succeeds`() {
        // given
        whenever(mockReactionUseCase.getAllForVlog(vlogId, false)).thenReturn(Single.just(combindUserReactionsModelList))
        // when
        viewModel.getReactions(vlogId, false)
        // then
        verify(mockReactionUseCase).getAllForVlog(vlogId, false)
        assertEquals(
            Resource(
                state = ResourceState.SUCCESS,
                data = reactionItemList,
                message = null
            ), viewModel.reactions.value
        )
    }

    @Test
    fun `get reactions fails`() {
        // given
        whenever(mockReactionUseCase.getAllForVlog(vlogId, true)).thenReturn(Single.error(throwable))
        // when
        viewModel.getReactions(vlogId, true)
        // then
        verify(mockReactionUseCase).getAllForVlog(vlogId, true)
        assertEquals(
            Resource(state = ResourceState.ERROR, data = null, message = throwable.message),
            viewModel.reactions.value
        )
    }
}
