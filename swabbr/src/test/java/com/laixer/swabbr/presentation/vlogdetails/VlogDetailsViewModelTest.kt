@file:Suppress("IllegalIdentifier")

package com.laixer.swabbr.presentation.vlogdetails

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.laixer.swabbr.presentation.RxSchedulersOverrideRule
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.swabbr.*
import com.laixer.swabbr.domain.usecase.UserReactionUseCase
import com.laixer.swabbr.domain.usecase.UsersVlogsUseCase
import com.laixer.swabbr.presentation.model.mapToPresentation
import io.reactivex.Single
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class VlogDetailsViewModelTest {

    private lateinit var viewModel: VlogDetailsViewModel

    private val mockUsersVlogsUseCase: UsersVlogsUseCase = mock()
    private val mockUserReactionUseCase: UserReactionUseCase = mock()

    private val combinedUserReaction = Pair(user, reaction)
    private val reactions = listOf(combinedUserReaction)

    private val vlogId = vlog.vlogId

    private val throwable = Throwable()

    @Rule
    @JvmField
    val rxSchedulersOverrideRule = RxSchedulersOverrideRule()

    @Rule
    @JvmField
    val instantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        viewModel = VlogDetailsViewModel(mockUsersVlogsUseCase, mockUserReactionUseCase)
    }

    @Test
    fun `get single vlog succeeds`() {
        // given
        val idList = arrayListOf(vlogId)
        whenever(mockUsersVlogsUseCase.get(idList, false))
            .thenReturn(Single.just(listOf(pairUserVlog)))

        // when
        viewModel.getVlogs(idList)

        // then
        verify(mockUsersVlogsUseCase).get(idList, false)
        assertEquals(pairUserVlog.mapToPresentation(), viewModel.vlogs.value!!.data!![0])
    }

    @Test
    fun `get multiple vlogs succeeds`() {
        // given
        val idList = arrayListOf(vlogId, vlogId)
        whenever(mockUsersVlogsUseCase.get(idList, false))
            .thenReturn(Single.just(listOf(pairUserVlog, pairUserVlog)))

        // when
        viewModel.getVlogs(idList)

        // then
        verify(mockUsersVlogsUseCase).get(idList, false)
        assertEquals(pairUserVlog.mapToPresentation(), viewModel.vlogs.value!!.data!![0])
        assertEquals(pairUserVlog.mapToPresentation(), viewModel.vlogs.value!!.data!![1])
    }

    @Test
    fun `get zero vlogs succeeds`() {
        // given
        val idList = arrayListOf<String>()
        whenever(mockUsersVlogsUseCase.get(idList, false))
            .thenReturn(Single.just(listOf()))

        // when
        viewModel.getVlogs(idList)

        // then
        verify(mockUsersVlogsUseCase).get(idList, false)
        assert(viewModel.vlogs.value!!.data!!.isNullOrEmpty())
    }

    @Test
    fun `get reactions succeeds`() {
        // given
        whenever(mockUserReactionUseCase.get(vlogId, false)).thenReturn(Single.just(reactions))

        // when
        viewModel.getReactions(vlogId, false)

        // then
        verify(mockUserReactionUseCase).get(vlogId, false)
        assertEquals(
            Resource(
                state = ResourceState.SUCCESS,
                data = reactions.mapToPresentation(),
                message = null
            ), viewModel.reactions.value
        )
    }

    @Test
    fun `get reactions fails`() {
        // given
        whenever(mockUserReactionUseCase.get(vlogId, true)).thenReturn(Single.error(throwable))

        // when
        viewModel.getReactions(vlogId, true)

        // then
        verify(mockUserReactionUseCase).get(vlogId, true)
        assertEquals(
            Resource(state = ResourceState.ERROR, data = null, message = throwable.message),
            viewModel.reactions.value
        )
    }
}
