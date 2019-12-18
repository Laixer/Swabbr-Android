@file:Suppress("IllegalIdentifier")

package com.laixer.swabbr.domain.usecase

import com.laixer.swabbr.domain.repository.AuthRepository
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.laixer.swabbr.login
import com.laixer.swabbr.registration
import com.laixer.swabbr.settings
import com.laixer.swabbr.user
import io.reactivex.Single
import org.junit.Before
import org.junit.Test

class AuthUseCaseTest {

    private lateinit var usecase: AuthUseCase

    private val mockRepository: AuthRepository = mock()

    private val reponse = Pair(Pair("token", user), settings)

    @Before
    fun setUp() {
        usecase = AuthUseCase(mockRepository)
    }

    @Test
    fun `login success`() {
        // given
        whenever(mockRepository.login(login)).thenReturn(Single.just(reponse))

        // when
        val test = usecase.login(login).test()

        // then
        verify(mockRepository).login(login)

        test.assertNoErrors()
        test.assertComplete()
        test.assertValueCount(1)
        test.assertValue(reponse)
    }

    @Test
    fun `login fail`() {
        // given
        val throwable = Throwable()
        whenever(mockRepository.login(login)).thenReturn(Single.error(throwable))

        // when
        val test = usecase.login(login).test()

        // then
        verify(mockRepository).login(login)

        test.assertNoValues()
        test.assertNotComplete()
        test.assertError(throwable)
        test.assertValueCount(0)
    }

    @Test
    fun `registration success`() {
        // given
        whenever(mockRepository.register(registration)).thenReturn(Single.just(reponse))

        // when
        val test = usecase.register(registration).test()

        // then
        verify(mockRepository).register(registration)

        test.assertNoErrors()
        test.assertComplete()
        test.assertValueCount(1)
        test.assertValue(reponse)
    }

    @Test
    fun `registration fail`() {
        // given
        val throwable = Throwable()
        whenever(mockRepository.register(registration)).thenReturn(Single.error(throwable))

        // when
        val test = usecase.register(registration).test()

        // then
        verify(mockRepository).register(registration)

        test.assertNoValues()
        test.assertNotComplete()
        test.assertError(throwable)
        test.assertValueCount(0)
    }
}
