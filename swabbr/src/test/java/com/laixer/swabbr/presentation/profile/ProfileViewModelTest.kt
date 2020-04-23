package com.laixer.swabbr.presentation.profile

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.swabbr.Items
import com.laixer.swabbr.Models
import com.laixer.swabbr.domain.usecase.FollowUseCase
import com.laixer.swabbr.domain.usecase.UserVlogsUseCase
import com.laixer.swabbr.domain.usecase.UsersUseCase
import com.laixer.swabbr.presentation.RxSchedulersOverrideRule
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.internal.matchers.apachecommons.ReflectionEquals

class ProfileViewModelTest {

    private lateinit var viewModel: ProfileViewModel
    private val mockUsersUseCase: UsersUseCase = mock()
    private val mockUserVlogsUseCase: UserVlogsUseCase = mock()
    private val mockFollowUseCase: FollowUseCase = mock()

    private val model = Models.user
    private val item = Items.user

    private val vlogModel = Models.vlog
    private val vlogModelList = listOf(vlogModel)

    private val vlogItem = Items.vlog
    private val vlogItemList = listOf(vlogItem)

    private val followRequestModel = Models.followRequest
    private val followRequestItem = Items.followRequest

    private val profileVlogs = Pair(model, vlogModelList)
    private val userId = Models.user.id
    private val throwable = Throwable()

    @Rule
    @JvmField
    val rxSchedulersOverrideRule = RxSchedulersOverrideRule()

    @Rule
    @JvmField
    val instantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        viewModel = ProfileViewModel(mockUsersUseCase, mockUserVlogsUseCase, mockFollowUseCase)
    }

    @Test
    fun `get profile succeeds`() {
        // given
        whenever(mockUsersUseCase.get(userId, false))
            .thenReturn(Single.just(model))
        // when
        viewModel.getProfile(userId, false)
        // then
        verify(mockUsersUseCase).get(userId, false)
        assertTrue(ReflectionEquals(item).matches(viewModel.profile.value))
    }

    @Test
    fun `get profilevlogs succeeds`() {
        // given
        whenever(mockUserVlogsUseCase.get(userId, false)).thenReturn(Single.just(profileVlogs.second))
        // when
        viewModel.getProfileVlogs(userId, false)
        // then
        verify(mockUserVlogsUseCase).get(userId, false)
        assertEquals(
            Resource(
                state = ResourceState.SUCCESS,
                data = vlogItemList,
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

    @Test
    fun `get followstatus succeeds`() {
        // given
        whenever(mockFollowUseCase.getFollowRequest(userId))
            .thenReturn(Single.just(followRequestModel))
        // when
        viewModel.getFollowRequest(userId)

        // then
        verify(mockFollowUseCase).getFollowRequest(userId)
        assertEquals(
            Resource(
                state = ResourceState.SUCCESS,
                data = followRequestItem,
                message = null
            ), viewModel.followRequest.value
        )
    }

    @Test
    fun `get followstatus fails`() {
        // given
        whenever(mockFollowUseCase.getFollowRequest(userId)).thenReturn(Single.error(throwable))
        // when
        viewModel.getFollowRequest(userId)
        // then
        verify(mockFollowUseCase).getFollowRequest(userId)
        assertEquals(
            Resource(state = ResourceState.ERROR, data = null, message = throwable.message),
            viewModel.followRequest.value
        )
    }
}
