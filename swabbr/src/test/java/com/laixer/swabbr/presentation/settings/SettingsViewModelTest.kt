package com.laixer.swabbr.presentation.settings

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.swabbr.Items
import com.laixer.swabbr.Models
import com.laixer.swabbr.domain.usecase.AuthUseCase
import com.laixer.swabbr.domain.usecase.SettingsUseCase
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

class SettingsViewModelTest {

    private lateinit var viewModel: SettingsViewModel
    private val mockSettingsUseCase: SettingsUseCase = mock()
    private val mockAuthUseCase: AuthUseCase = mock()

    private val model = Models.settings
    private val item = Items.settings

    private val throwable = Throwable()

    @Rule
    @JvmField
    val rxSchedulersOverrideRule = RxSchedulersOverrideRule()

    @Rule
    @JvmField
    val instantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        viewModel = SettingsViewModel(mockSettingsUseCase, mockAuthUseCase)
    }

    @Test
    fun `get settings succeeds`() {
        // given
        whenever(mockSettingsUseCase.get(false))
            .thenReturn(Single.just(model))
        // when
        viewModel.getSettings(false)
        // then
        verify(mockSettingsUseCase).get(false)
        assertEquals(
            Resource(ResourceState.SUCCESS, item, null),
            viewModel.settings.value
        )
    }

    @Test
    fun `get settings fails`() {
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
