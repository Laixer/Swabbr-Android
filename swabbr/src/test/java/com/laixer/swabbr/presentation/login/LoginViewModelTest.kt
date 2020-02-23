package com.laixer.swabbr.presentation.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.laixer.swabbr.presentation.RxSchedulersOverrideRule
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.swabbr.domain.usecase.AuthUseCase
import com.laixer.swabbr.login
import com.laixer.swabbr.settings
import com.laixer.swabbr.user
import io.reactivex.Single
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class LoginViewModelTest {

    private lateinit var viewModel: LoginViewModel

    private val mockAuthUseCase: AuthUseCase = mock()

    private val throwable = Throwable()

    private val reponse = Pair(Pair("token", user), settings)

    @Rule
    @JvmField
    val rxSchedulersOverrideRule = RxSchedulersOverrideRule()

    @Rule
    @JvmField
    val instantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        viewModel = LoginViewModel(mockAuthUseCase)
    }

    @Test
    fun `login succeeds`() {
        // given
        whenever(mockAuthUseCase.login(login))
            .thenReturn(Single.just(reponse))

        // when
        viewModel.login(login)

        // then
        verify(mockAuthUseCase).login(login)
        assertEquals(
            Resource(ResourceState.SUCCESS, true, null),
            viewModel.authorized.value)
    }

    @Test
    fun `login fails`() {
        // given
        whenever(mockAuthUseCase.login(login)).thenReturn(Single.error(throwable))

        // when
        viewModel.login(login)

        // then
        verify(mockAuthUseCase).login(login)
        assertEquals(
            Resource(state = ResourceState.ERROR, data = null, message = throwable.message),
            viewModel.authorized.value
        )
    }
}
