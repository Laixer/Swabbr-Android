package com.laixer.swabbr.domain.usecase

import com.laixer.swabbr.Models
import com.laixer.swabbr.domain.interfaces.UserRepository
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import org.junit.Before
import org.junit.Test

class UsersUseCaseTest {

    private lateinit var usecase: UsersUseCase
    private val mockRepository: UserRepository = mock()

    private val vlogId = Models.user.id
    private val model = Models.user
    private val list = listOf(Models.user)

    @Before
    fun setUp() {
        usecase = UsersUseCase(mockRepository)
    }

    @Test
    fun `repository get success`() {
        // given
        whenever(mockRepository.get(vlogId, false)).thenReturn(Single.just(model))
        // when
        val test = usecase.get(vlogId, false).test()
        // then
        verify(mockRepository).get(vlogId, false)

        test.assertNoErrors()
        test.assertComplete()
        test.assertValueCount(1)
        test.assertValue(model)
    }

    @Test
    fun `repository get fail`() {
        // given
        val throwable = Throwable()
        whenever(mockRepository.get(vlogId, false)).thenReturn(Single.error(throwable))
        // when
        val test = usecase.get(vlogId, false).test()
        // then
        verify(mockRepository).get(vlogId, false)

        test.assertNoValues()
        test.assertNotComplete()
        test.assertError(throwable)
        test.assertValueCount(0)
    }

    @Test
    fun `search with result`() {
        // given
        whenever(mockRepository.search(model.firstName)).thenReturn(Single.just(listOf(model)))
        // when
        val test = usecase.search(model.firstName).test()
        // then
        verify(mockRepository).search(model.firstName)

        test.assertValue(listOf(model))
        test.assertComplete()
    }

    @Test
    fun `search no result`() {
        // given
        whenever(mockRepository.search(model.firstName)).thenReturn(Single.just(emptyList()))
        // when
        val test = usecase.search(model.firstName).test()
        // then
        verify(mockRepository).search(model.firstName)

        test.assertValue(emptyList())
        test.assertComplete()
        test.assertNoErrors()
    }

    @Test
    fun `search fail`() {
        // given
        val throwable = Throwable()
        whenever(mockRepository.search(model.firstName)).thenReturn(Single.error(throwable))
        // when
        val test = usecase.search(model.firstName).test()
        // then
        verify(mockRepository).search(model.firstName)

        test.assertNoValues()
        test.assertNotComplete()
        test.assertError(throwable)
        test.assertValueCount(0)
    }
}
