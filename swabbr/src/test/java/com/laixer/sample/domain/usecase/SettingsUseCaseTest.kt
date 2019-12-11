@file:Suppress("IllegalIdentifier")

package com.laixer.swabbr.domain.usecase

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.laixer.swabbr.domain.repository.SettingsRepository
import com.laixer.swabbr.settings
import io.reactivex.Single
import org.junit.Before
import org.junit.Test

class SettingsUseCaseTest {

    private lateinit var usecase: SettingsUseCase

    private val mockRepository: SettingsRepository = mock()

    @Before
    fun setUp() {
        usecase = SettingsUseCase(mockRepository)
    }

    @Test
    fun `repository get success`() {
        // given
        whenever(mockRepository.get(false)).thenReturn(Single.just(settings))

        // when
        val test = usecase.get(false).test()

        // then
        verify(mockRepository).get(false)

        test.assertNoErrors()
        test.assertComplete()
        test.assertValueCount(1)
        test.assertValue(settings)
    }

    @Test
    fun `repository get fail`() {
        // given
        val throwable = Throwable()
        whenever(mockRepository.get(false)).thenReturn(Single.error(throwable))

        // when
        val test = usecase.get(false).test()

        // then
        verify(mockRepository).get(false)

        test.assertNoValues()
        test.assertNotComplete()
        test.assertError(throwable)
        test.assertValueCount(0)
    }
}
