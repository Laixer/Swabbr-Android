package com.laixer.swabbr.presentation.auth.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.laixer.swabbr.utils.resources.Resource
import com.laixer.swabbr.utils.resources.ResourceState
import com.laixer.swabbr.Items
import com.laixer.swabbr.Models
import com.laixer.swabbr.domain.usecase.AuthUseCase
import com.laixer.swabbr.presentation.RxSchedulersOverrideRule
import com.laixer.swabbr.presentation.auth.AuthViewModel
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class AuthViewModelTest {

    private lateinit var viewModel: AuthViewModel
    private val mockAuthUseCase: AuthUseCase = mock()
    private val throwable = Throwable()
    private val authUserModel = Models.authUser
    private val authUserItem = Items.authUser
    private val loginModel = Models.login
    private val loginItem = Items.login
    private val registerModel = Models.registration
    private val registerItem = Items.registration
    @Rule
    @JvmField
    val rxSchedulersOverrideRule = RxSchedulersOverrideRule()
    @Rule
    @JvmField
    val instantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        viewModel = AuthViewModel(mockAuthUseCase)
    }

    @Test
    fun `get authenticated user succeeds`() {
        // given
        whenever(mockAuthUseCase.getAuthenticatedUser()).thenReturn(Single.just(authUserModel))
        // when
        viewModel.get()
        // then
        verify(mockAuthUseCase).getAuthenticatedUser()
        assertEquals(
            Resource(ResourceState.SUCCESS, authUserItem, null),
            viewModel.authenticatedUser.value
        )
    }

    @Test
    fun `get authenticated user fails`() {
        // given
        whenever(mockAuthUseCase.getAuthenticatedUser()).thenReturn(Single.error(throwable))
        // when
        viewModel.get()
        // then
        verify(mockAuthUseCase).getAuthenticatedUser()
        assertEquals(
            Resource(state = ResourceState.ERROR, data = null, message = throwable.message),
            viewModel.authenticatedUser.value
        )
    }

    @Test
    fun `login succeeds`() {
        // given
        whenever(mockAuthUseCase.login(loginModel)).thenReturn(Single.just(authUserModel))
        // when
        viewModel.login(loginItem)
        // then
        verify(mockAuthUseCase).login(loginModel)
        assertEquals(
            Resource(ResourceState.SUCCESS, authUserItem, null),
            viewModel.authenticatedUser.value
        )
    }

    @Test
    fun `login fails`() {
        // given
        whenever(mockAuthUseCase.login(loginModel)).thenReturn(Single.error(throwable))
        // when
        viewModel.login(loginItem)
        // then
        verify(mockAuthUseCase).login(loginModel)
        assertEquals(
            Resource(state = ResourceState.ERROR, data = null, message = throwable.message),
            viewModel.authenticatedUser.value
        )
    }

    @Test
    fun `registration succeeds`() {
        // given
        whenever(mockAuthUseCase.register(registerModel))
            .thenReturn(Single.just(authUserModel))
        // when
        viewModel.register(registerItem)
        // then
        verify(mockAuthUseCase).register(registerModel)
        assertEquals(
            Resource(ResourceState.SUCCESS, authUserItem, null),
            viewModel.authenticatedUser.value
        )
    }

    @Test
    fun `registration fails`() {
        // given
        whenever(mockAuthUseCase.register(registerModel)).thenReturn(Single.error(throwable))
        // when
        viewModel.register(registerItem)
        // then
        verify(mockAuthUseCase).register(registerModel)
        assertEquals(
            Resource(state = ResourceState.ERROR, data = null, message = throwable.message),
            viewModel.authenticatedUser.value
        )
    }
}
