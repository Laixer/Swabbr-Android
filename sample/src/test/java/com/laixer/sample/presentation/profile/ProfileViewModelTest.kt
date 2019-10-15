@file:Suppress("IllegalIdentifier")

package com.laixer.sample.presentation.profile

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.laixer.sample.presentation.RxSchedulersOverrideRule
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.sample.*
import com.laixer.sample.domain.usecase.UserVlogsUseCase
import com.laixer.sample.domain.usecase.UsersUseCase
import com.laixer.sample.presentation.model.mapToPresentation
import io.reactivex.Single
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class ProfileViewModelTest {

    private lateinit var viewModel: ProfileViewModel

    private val mockUsersUseCase: UsersUseCase = mock()
    private val mockUserVlogsUseCase: UserVlogsUseCase = mock()

    private val profileVlogs = Pair(user, listOf(vlog))

    private val userId = user.id

    private val throwable = Throwable()

    @Rule
    @JvmField
    val rxSchedulersOverrideRule = RxSchedulersOverrideRule()

    @Rule
    @JvmField
    val instantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        viewModel = ProfileViewModel(mockUsersUseCase, mockUserVlogsUseCase)
    }

    @Test
    fun `get profile succeeds`() {
        // given
        whenever(mockUsersUseCase.get(userId, false))
            .thenReturn(Single.just(user))

        // when
        viewModel.getProfile(userId, false)

        // then
        verify(mockUsersUseCase).get(userId, false)
        assertEquals(user.mapToPresentation(), viewModel.profile.value)
    }

    @Test
    fun `get profilevlogs succeeds`() {
        // given
        whenever(mockUserVlogsUseCase.get(userId, false)).thenReturn(Single.just(profileVlogs))

        // when
        viewModel.getProfileVlogs(userId, false)

        // then
        verify(mockUserVlogsUseCase).get(userId, false)
        assertEquals(
            Resource(
                state = ResourceState.SUCCESS,
                data = profileVlogs.second.mapToPresentation(),
                message = null
            ), viewModel.profileVlogs.value
        )
    }

    @Test
    fun `get profilevlogs fails`() {
        // given
        whenever(mockUserVlogsUseCase.get(userId, true)).thenReturn(Single.error(throwable))

        // when
        viewModel.getProfileVlogs(userId, true)

        // then
        verify(mockUserVlogsUseCase).get(userId, true)
        assertEquals(
            Resource(state = ResourceState.ERROR, data = null, message = throwable.message),
            viewModel.profileVlogs.value
        )
    }
}