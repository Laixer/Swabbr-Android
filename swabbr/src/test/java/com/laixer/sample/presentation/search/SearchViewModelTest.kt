@file:Suppress("IllegalIdentifier")

package com.laixer.swabbr.presentation.search

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.laixer.swabbr.presentation.RxSchedulersOverrideRule
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.swabbr.*
import com.laixer.swabbr.domain.usecase.UsersUseCase
import com.laixer.swabbr.presentation.model.mapToPresentation
import io.reactivex.Single
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class SearchViewModelTest {

    private lateinit var viewModel: SearchViewModel

    private val mockUsersUseCase: UsersUseCase = mock()

    private val query = user.id

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
        whenever(mockUsersUseCase.get(query, false))
            .thenReturn(Single.just(user))

        // when
        viewModel.getProfiles(query)

        // then
        verify(mockUsersUseCase).get(query, false)
        assertEquals(
            Resource(ResourceState.SUCCESS, listOf(user.mapToPresentation()), null),
            viewModel.profiles.value)
    }

    @Test
    fun `get profiles fails`() {
        // given
        whenever(mockUsersUseCase.get(query, true)).thenReturn(Single.error(throwable))

        // when
        viewModel.getProfiles(query, true)

        // then
        verify(mockUsersUseCase).get(query, true)
        assertEquals(
            Resource(state = ResourceState.ERROR, data = null, message = throwable.message),
            viewModel.profiles.value
        )
    }
}