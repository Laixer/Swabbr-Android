package com.laixer.swabbr.domain.usecase

import com.laixer.swabbr.Models
import com.laixer.swabbr.domain.repository.ReactionRepository
import com.laixer.swabbr.domain.repository.UserRepository
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import org.junit.Before
import org.junit.Test

class ReactionUseCaseTest {

    private lateinit var reactionUseCase: ReactionUseCase
    private val mockUserRepository: UserRepository = mock()
    private val mockReactionRepository: ReactionRepository = mock()

    private val vlogId = Models.vlog.id
    private val userId = Models.vlog.userId

    private val user = Models.user
    private val reaction = Models.reaction

    private val list = listOf(reaction)

    @Before
    fun setUp() {
        reactionUseCase = ReactionUseCase(mockUserRepository, mockReactionRepository)
    }

    @Test
    fun `repository get success`() {
        // given
        whenever(mockUserRepository.get(userId, false)).thenReturn(Single.just(user))
        whenever(mockReactionRepository.get(vlogId, false)).thenReturn(Single.just(list))
        // when
        val test = reactionUseCase.getAllForVlog(vlogId, false).test()
        // then
        verify(mockUserRepository).get(userId, false)
        verify(mockReactionRepository).get(vlogId, false)

        test.assertNoErrors()
        test.assertComplete()
        test.assertValueCount(1)
        test.assertValue(listOf(Pair(user, reaction)))
    }

    @Test
    fun `repository get fail`() {
        // given
        val throwable = Throwable()
        whenever(mockReactionRepository.get(vlogId, false)).thenReturn(Single.error(throwable))
        // when
        val test = reactionUseCase.getAllForVlog(vlogId, false).test()
        // then
        verify(mockReactionRepository).get(vlogId, false)

        test.assertNoValues()
        test.assertNotComplete()
        test.assertError(throwable)
        test.assertValueCount(0)
    }
}
