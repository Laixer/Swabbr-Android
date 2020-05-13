package com.laixer.swabbr.domain.usecase

import com.laixer.swabbr.Models
import com.laixer.swabbr.domain.model.AuthUser
import com.laixer.swabbr.domain.repository.AuthRepository
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import org.junit.Before
import org.junit.Test

class AuthUseCaseTest {

    private lateinit var usecase: AuthUseCase
    private val mockAuthRepository: AuthRepository = mock()
    private val response = AuthUser("token", Models.user, Models.settings)

    private val login = Models.login
    private val registration = Models.registration

    private val user = Models.user
    private val settings = Models.settings

    @Before
    fun setUp() {
        usecase = AuthUseCase(mockAuthRepository)
    }

    @Test
    fun `login success`() {
        // given
        whenever(mockAuthRepository.login(login)).thenReturn(Single.just(response))
        // when
        val test = usecase.login(login).test()
        // then
        verify(mockAuthRepository).login(login)

        test.assertNoErrors()
        test.assertComplete()
        test.assertValueCount(1)
        test.assertValue(response)
    }

    @Test
    fun `login fail`() {
        // given
        val throwable = Throwable()
        whenever(mockAuthRepository.login(login)).thenReturn(Single.error(throwable))
        // when
        val test = usecase.login(login).test()
        // then
        verify(mockAuthRepository).login(login)

        test.assertNoValues()
        test.assertNotComplete()
        test.assertError(throwable)
        test.assertValueCount(0)
    }

    @Test
    fun `registration success`() {
        // given
        whenever(mockAuthRepository.register(registration)).thenReturn(Single.just(response))
        // when
        val test = usecase.register(registration).test()
        // then
        verify(mockAuthRepository).register(registration)

        test.assertNoErrors()
        test.assertComplete()
        test.assertValueCount(1)
        test.assertValue(response)
    }

    @Test
    fun `registration fail`() {
        // given
        val throwable = Throwable()
        whenever(mockAuthRepository.register(registration)).thenReturn(Single.error(throwable))
        // when
        val test = usecase.register(registration).test()
        // then
        verify(mockAuthRepository).register(registration)

        test.assertNoValues()
        test.assertNotComplete()
        test.assertError(throwable)
        test.assertValueCount(0)
    }
}
