package com.laixer.swabbr.presentation.profile

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.laixer.swabbr.utils.resources.Resource
import com.laixer.swabbr.utils.resources.ResourceState
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

    private val followStatusModel = Models.followStatus
    private val followStatusItem = Items.followStatus

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
        viewModel.getUser(userId, false)
        // then
        verify(mockUsersUseCase).get(userId, false)
        assertTrue(ReflectionEquals(item).matches(viewModel.user.value))
    }

    @Test
    fun `get profilevlogs succeeds`() {
        // given
        whenever(mockUserVlogsUseCase.getAllFromUser(userId, false)).thenReturn(Single.just(profileVlogs.second))
        // when
        viewModel.getVlogsByUser(userId, false)
        // then
        verify(mockUserVlogsUseCase).getAllFromUser(userId, false)
        assertEquals(
            Resource(
                state = ResourceState.SUCCESS,
                data = vlogItemList,
                message = null
            ), viewModel.userVlogs.value
        )
    }

    @Test
    fun `get profilevlogs fails`() {
        // given
        whenever(mockUserVlogsUseCase.getAllFromUser(userId, true)).thenReturn(Single.error(throwable))
        // when
        viewModel.getVlogsByUser(userId, true)
        // then
        verify(mockUserVlogsUseCase).getAllFromUser(userId, true)
        assertEquals(
            Resource(state = ResourceState.ERROR, data = null, message = throwable.message),
            viewModel.userVlogs.value
        )
    }

    @Test
    fun `get followstatus succeeds`() {
        // given
        whenever(mockFollowUseCase.getFollowStatus(userId))
            .thenReturn(Single.just(followStatusModel))
        // when
        viewModel.getFollowRequestAsCurrentUser(userId)

        // then
        verify(mockFollowUseCase).getFollowStatus(userId)
        assertEquals(
            Resource(
                state = ResourceState.SUCCESS,
                data = followRequestItem,
                message = null
            ), viewModel.followRequestAsCurrentUser.value
        )
    }

    @Test
    fun `get followstatus fails`() {
        // given
        whenever(mockFollowUseCase.getFollowStatus(userId)).thenReturn(Single.error(throwable))
        // when
        viewModel.getFollowRequestAsCurrentUser(userId)
        // then
        verify(mockFollowUseCase).getFollowStatus(userId)
        assertEquals(
            Resource(state = ResourceState.ERROR, data = null, message = throwable.message),
            viewModel.followRequestAsCurrentUser.value
        )
    }
}
