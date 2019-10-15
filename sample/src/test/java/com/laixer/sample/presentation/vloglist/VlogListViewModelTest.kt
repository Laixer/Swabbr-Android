@file:Suppress("IllegalIdentifier")

package com.laixer.sample.presentation.vloglist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.laixer.sample.presentation.RxSchedulersOverrideRule
import com.laixer.sample.presentation.model.mapToPresentation
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.sample.domain.usecase.UsersVlogsUseCase
import com.laixer.sample.pairUserVlog
import io.reactivex.Single
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class VlogListViewModelTest {

    private lateinit var viewModel: VlogListViewModel

    private val mockUseCase: UsersVlogsUseCase = mock()

    private val pairUserVlogList = listOf(pairUserVlog)
    private val throwable = Throwable()

    @Rule
    @JvmField
    val rxSchedulersOverrideRule = RxSchedulersOverrideRule()

    @Rule
    @JvmField
    val instantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        viewModel = VlogListViewModel(mockUseCase)
    }

    @Test
    fun `get vlog item list succeeds`() {
        // given
        whenever(mockUseCase.get(false)).thenReturn(Single.just(pairUserVlogList))

        // when
        viewModel.get(false)

        // then
        verify(mockUseCase).get(false)
        assertEquals(
            Resource(ResourceState.SUCCESS, pairUserVlogList.map { Pair(it.first.mapToPresentation(), it.second.mapToPresentation()) }, null),
            viewModel.vlogs.value
        )
    }

    @Test
    fun `get vlog item list fails`() {
        // given
        whenever(mockUseCase.get(false)).thenReturn(Single.error(throwable))

        // when
        viewModel.get(false)

        // then
        verify(mockUseCase).get(false)
        assertEquals(
            Resource(ResourceState.ERROR, null, throwable.message),
            viewModel.vlogs.value
        )
    }
}
