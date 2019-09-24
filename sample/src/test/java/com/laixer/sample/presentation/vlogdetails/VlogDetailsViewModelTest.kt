@file:Suppress("IllegalIdentifier")

package com.laixer.sample.presentation.vlogdetails

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.laixer.sample.domain.usecase.ReactionsUseCase
import com.laixer.sample.presentation.RxSchedulersOverrideRule
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.sample.*
import com.laixer.sample.domain.usecase.UserVlogUseCase
import com.laixer.sample.presentation.model.mapToPresentation
import io.reactivex.Single
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class VlogDetailsViewModelTest {

    private lateinit var viewModel: VlogDetailsViewModel

    private val mockUserVlogUseCase: UserVlogUseCase = mock()
    private val mockReactionsUseCase: ReactionsUseCase = mock()

    private val reactions = listOf(reaction)

    private val userId = user.id
    private val vlogId = vlog.id

    private val throwable = Throwable()

    @Rule
    @JvmField
    val rxSchedulersOverrideRule = RxSchedulersOverrideRule()

    @Rule
    @JvmField
    val instantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        viewModel = VlogDetailsViewModel(mockUserVlogUseCase, mockReactionsUseCase)
    }

    @Test
    fun `get vlog succeeds`() {
        // given
        whenever(mockUserVlogUseCase.get(userId, vlogId, false))
            .thenReturn(Single.just(combinedUserVlog))

        // when
        viewModel.getVlogs(UserIdVlogId(userId, vlogId))

        // then
        verify(mockUserVlogUseCase).get(userId, vlogId, false)
        assertEquals(combinedUserVlog.mapToPresentation(), viewModel.vlogs.value)
    }

    @Test
    fun `get reactions succeeds`() {
        // given
        whenever(mockReactionsUseCase.get(vlogId, false)).thenReturn(Single.just(reactions))

        // when
        viewModel.getReactions(vlogId, false)

        // then
        verify(mockReactionsUseCase).get(vlogId, false)
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
        whenever(mockReactionsUseCase.get(vlogId, true)).thenReturn(Single.error(throwable))

        // when
        viewModel.getReactions(vlogId, true)

        // then
        verify(mockReactionsUseCase).get(vlogId, true)
        assertEquals(
            Resource(state = ResourceState.ERROR, data = null, message = throwable.message),
            viewModel.reactions.value
        )
    }
}
