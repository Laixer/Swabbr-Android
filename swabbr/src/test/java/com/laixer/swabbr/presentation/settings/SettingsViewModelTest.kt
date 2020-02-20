@file:Suppress("IllegalIdentifier")

package com.laixer.swabbr.presentation.settings

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.swabbr.domain.usecase.SettingsUseCase
import com.laixer.swabbr.presentation.RxSchedulersOverrideRule
import com.laixer.swabbr.presentation.model.mapToPresentation
import com.laixer.swabbr.settings
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class SettingsViewModelTest {

    private lateinit var viewModel: SettingsViewModel
    private val mockSettingsUseCase: SettingsUseCase = mock()
    private val throwable = Throwable()
    @Rule
    @JvmField
    val rxSchedulersOverrideRule = RxSchedulersOverrideRule()
    @Rule
    @JvmField
    val instantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        viewModel = SettingsViewModel(mockSettingsUseCase)
    }

    @Test
    fun `get profiles succeeds`() {
        // given
        whenever(mockSettingsUseCase.get(false))
            .thenReturn(Single.just(settings))
        // when
        viewModel.getSettings(false)
        // then
        verify(mockSettingsUseCase).get(false)
        assertEquals(
            Resource(ResourceState.SUCCESS, settings.mapToPresentation(), null),
            viewModel.settings.value
        )
    }

    @Test
    fun `get profiles fails`() {
        // given
        whenever(mockSettingsUseCase.get(true)).thenReturn(Single.error(throwable))
        // when
        viewModel.getSettings(true)
        // then
        verify(mockSettingsUseCase).get(true)
        assertEquals(
            Resource(state = ResourceState.ERROR, data = null, message = throwable.message),
            viewModel.settings.value
        )
    }
}
