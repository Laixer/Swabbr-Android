@file:Suppress("IllegalIdentifier")

package com.laixer.sample.domain.usecase

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.laixer.sample.domain.repository.ReactionRepository
import com.laixer.sample.reaction
import com.laixer.sample.vlog
import io.reactivex.Single
import org.junit.Before
import org.junit.Test

class ReactionsUseCaseTest {

    private lateinit var usecase: ReactionsUseCase

    private val mockRepository: ReactionRepository = mock()

    private val vlogId = vlog.id
    private val reactionList = listOf(reaction)

    @Before
    fun setUp() {
        usecase = ReactionsUseCase(mockRepository)
    }

    @Test
    fun `repository get success`() {
        // given
        whenever(mockRepository.get(vlogId, false)).thenReturn(Single.just(reactionList))

        // when
        val test = usecase.get(vlogId, false).test()

        // then
        verify(mockRepository).get(vlogId, false)

        test.assertNoErrors()
        test.assertComplete()
        test.assertValueCount(1)
        test.assertValue(reactionList)
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
}
