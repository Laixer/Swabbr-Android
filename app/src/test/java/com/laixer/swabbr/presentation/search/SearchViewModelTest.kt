package com.laixer.swabbr.presentation.search

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.swabbr.Items
import com.laixer.swabbr.Models
import com.laixer.swabbr.domain.usecase.UsersUseCase
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

class SearchViewModelTest {

    private lateinit var viewModel: SearchViewModel
    private val mockUsersUseCase: UsersUseCase = mock()

    private val model = Models.user
    private val modelList = listOf(model)
    private val item = Items.user
    private val itemList = listOf(item)

    private val query = model.firstName

    private val throwable = Throwable()

    @Rule
    @JvmField
    val rxSchedulersOverrideRule = RxSchedulersOverrideRule()

    @Rule
    @JvmField
    val instantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        viewModel = SearchViewModel(mockUsersUseCase)
    }

    @Test
    fun `get profiles succeeds`() {
        // given
        whenever(mockUsersUseCase.search(query))
            .thenReturn(Single.just(modelList))
        // when
        viewModel.search(query)
        // then
        verify(mockUsersUseCase).search(query)
        assertEquals(
            Resource(ResourceState.SUCCESS, itemList, null),
            viewModel.users.value
        )
    }

    @Test
    fun `get profiles fails`() {
        // given
        whenever(mockUsersUseCase.search(query)).thenReturn(Single.error(throwable))
        // when
        viewModel.search(query)
        // then
        verify(mockUsersUseCase).search(query)
        assertEquals(
            Resource(state = ResourceState.ERROR, data = null, message = throwable.message),
            viewModel.users.value
        )
    }
}
