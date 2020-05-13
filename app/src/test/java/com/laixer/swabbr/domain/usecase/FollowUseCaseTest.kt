package com.laixer.swabbr.domain.usecase

import com.laixer.swabbr.Models
import com.laixer.swabbr.domain.repository.FollowRepository
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import org.junit.Before
import org.junit.Test

class FollowUseCaseTest {

    private lateinit var usecase: FollowUseCase
    private val mockRepository: FollowRepository = mock()

    private val userId = Models.user.id
    private val followers = listOf(Models.user)
    private val following = listOf(Models.user)

    @Before
    fun setUp() {
        usecase = FollowUseCase(mockRepository)
    }

    @Test
    fun `repository get success`() {
        // given
        whenever(mockRepository.getFollowers(userId)).thenReturn(Single.just(followers))
        // when
        val test = usecase.getFollowers(userId).test()
        // then
        verify(mockRepository).getFollowers(userId)

        test.assertNoErrors()
        test.assertComplete()
        test.assertValueCount(1)
        test.assertValue(followers)
    }

    @Test
    fun `repository get fail`() {
        // given
        val throwable = Throwable()
        whenever(mockRepository.getFollowers(userId)).thenReturn(Single.error(throwable))
        // when
        val test = usecase.getFollowers(userId).test()
        // then
        verify(mockRepository).getFollowers(userId)

        test.assertNoValues()
        test.assertNotComplete()
        test.assertError(throwable)
        test.assertValueCount(0)
    }
}
