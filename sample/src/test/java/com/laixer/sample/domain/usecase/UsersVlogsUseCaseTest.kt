@file:Suppress("IllegalIdentifier")

package com.laixer.sample.domain.usecase

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.laixer.sample.domain.repository.VlogRepository
import com.laixer.sample.domain.repository.UserRepository
import com.laixer.sample.vlog
import com.laixer.sample.user
import io.reactivex.Single
import org.junit.Before
import org.junit.Test

class UsersVlogsUseCaseTest {

    private lateinit var usersVlogsUseCase: UsersVlogsUseCase

    private val mockUserRepository: UserRepository = mock()
    private val mockVlogRepository: VlogRepository = mock()

    private val userList = listOf(user)
    private val vlogList = listOf(vlog)

    @Before
    fun setUp() {
        usersVlogsUseCase = UsersVlogsUseCase(mockUserRepository, mockVlogRepository)
    }

    @Test
    fun `repository get success`() {
        // given
        whenever(mockUserRepository.get(false)).thenReturn(Single.just(userList))
        whenever(mockVlogRepository.get(false)).thenReturn(Single.just(vlogList))

        // when
        val test = usersVlogsUseCase.get(false).test()

        // then
        verify(mockUserRepository).get(false)
        verify(mockVlogRepository).get(false)

        test.assertNoErrors()
        test.assertComplete()
        test.assertValueCount(1)
        test.assertValue(map(userList, vlogList))
    }

    @Test
    fun `repository get fail`() {
        // given
        val throwable = Throwable()
        whenever(mockUserRepository.get(false)).thenReturn(Single.error(throwable))
        whenever(mockVlogRepository.get(false)).thenReturn(Single.error(throwable))

        // when
        val test = usersVlogsUseCase.get(false).test()

        // then
        verify(mockUserRepository).get(false)
        verify(mockVlogRepository).get(false)

        test.assertNoValues()
        test.assertNotComplete()
        test.assertError(throwable)
        test.assertValueCount(0)
    }
}

class UserVlogUseCaseTest {

    private lateinit var userVlogUseCase: UserVlogUseCase

    private val mockUserRepository: UserRepository = mock {}
    private val mockVlogRepository: VlogRepository = mock {}

    private val userId = user.id
    private val vlogId = vlog.id

    @Before
    fun setUp() {
        userVlogUseCase = UserVlogUseCase(mockUserRepository, mockVlogRepository)
    }

    @Test
    fun `repository get success`() {
        // given
        whenever(mockUserRepository.get(false)).thenReturn(Single.just(listOf(user)))
        whenever(mockVlogRepository.get(vlogId, false)).thenReturn(Single.just(vlog))

        // when
        val test = userVlogUseCase.get(vlogId, false).test()

        // then
        verify(mockUserRepository).get(false)
        verify(mockVlogRepository).get(vlogId, false)

        test.assertNoErrors()
        test.assertComplete()
        test.assertValueCount(1)
        test.assertValue(map(listOf(user), vlog))
    }

    @Test
    fun `repository get fail`() {
            // given
            val throwable = Throwable()
            whenever(mockUserRepository.get(false)).thenReturn(Single.error(throwable))
            whenever(mockVlogRepository.get(vlogId, false)).thenReturn(Single.error(throwable))

            // when
            val test = userVlogUseCase.get(vlogId, false).test()

            // then
            verify(mockUserRepository).get(false)
            verify(mockVlogRepository).get(vlogId, false)

            test.assertNoValues()
            test.assertNotComplete()
            test.assertError(throwable)
            test.assertValueCount(0)
    }
}
