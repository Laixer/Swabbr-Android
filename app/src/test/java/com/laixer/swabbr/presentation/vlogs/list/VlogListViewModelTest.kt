package com.laixer.swabbr.presentation.vlogs.list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.laixer.swabbr.utils.resources.Resource
import com.laixer.swabbr.utils.resources.ResourceState
import com.laixer.swabbr.Items
import com.laixer.swabbr.Models
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

class VlogListViewModelTest {

    private lateinit var viewModel: VlogListViewModel
    private val mockUsersVlogsUseCase: VlogUseCase = mock()
    private val mockVlogUseCase: VlogUseCase = mock()


    private val userModel = Models.user
    private val userItem = Items.user

    private val vlogModel = Models.vlog
    private val vlogItem = Items.vlog

    private val pairUserVlogModel = Pair(userModel, vlogModel)
    private val pairUserVlogModelList = listOf(pairUserVlogModel)
    private val pairUserVlogItem = Items.uservlog
    private val pairUserVlogItemList = listOf(pairUserVlogItem)

    private val throwable = Throwable()

    @Rule
    @JvmField
    val rxSchedulersOverrideRule = RxSchedulersOverrideRule()

    @Rule
    @JvmField
    val instantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        viewModel = VlogListViewModel(mockUsersVlogsUseCase, mockVlogUseCase)
    }

    @Test
    fun `get featured vlog item list succeeds`() {
        // given
        whenever(mockUsersVlogsUseCase.getRecommendedVlogs(false)).thenReturn(Single.just(pairUserVlogModelList))
        // when
        viewModel.get(false)
        // then
        verify(mockUsersVlogsUseCase).getRecommendedVlogs(false)
        assertEquals(
            Resource(
                ResourceState.SUCCESS,
                pairUserVlogItemList,
                null
            ),
            viewModel.vlogs.value
        )
    }

    @Test
    fun `get featured vlog item list fails`() {
        // given
        whenever(mockUsersVlogsUseCase.getRecommendedVlogs(false)).thenReturn(Single.error(throwable))
        // when
        viewModel.get(false)
        // then
        verify(mockUsersVlogsUseCase).getRecommendedVlogs(false)
        assertEquals(
            Resource(ResourceState.ERROR, null, throwable.message),
            viewModel.vlogs.value
        )
    }
}
