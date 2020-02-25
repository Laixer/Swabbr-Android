package com.laixer.swabbr.presentation.registration

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.swabbr.domain.model.AuthUser
import com.laixer.swabbr.domain.usecase.AuthUseCase
import com.laixer.swabbr.presentation.RxSchedulersOverrideRule
import com.laixer.swabbr.registration
import com.laixer.swabbr.settings
import com.laixer.swabbr.user
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class RegistrationViewModelTest {

    private lateinit var viewModel: RegistrationViewModel
    private val mockAuthUseCase: AuthUseCase = mock()
    private val throwable = Throwable()
    private val reponse = AuthUser("token", user, settings)
    @Rule
    @JvmField
    val rxSchedulersOverrideRule = RxSchedulersOverrideRule()
    @Rule
    @JvmField
    val instantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        viewModel = RegistrationViewModel(mockAuthUseCase)
    }

    @Test
    fun `registration succeeds`() {
        // given
        whenever(mockAuthUseCase.register(registration))
            .thenReturn(Single.just(reponse))
        // when
        viewModel.register(registration)
        // then
        verify(mockAuthUseCase).register(registration)
        assertEquals(
            Resource(ResourceState.SUCCESS, true, null),
            viewModel.authorized.value
        )
    }

    @Test
    fun `registration fails`() {
        // given
        whenever(mockAuthUseCase.register(registration)).thenReturn(Single.error(throwable))
        // when
        viewModel.register(registration)
        // then
        verify(mockAuthUseCase).register(registration)
        assertEquals(
            Resource(state = ResourceState.ERROR, data = null, message = throwable.message),
            viewModel.authorized.value
        )
    }
}
