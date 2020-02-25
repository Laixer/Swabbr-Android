package com.laixer.swabbr.domain.usecase

import com.laixer.swabbr.domain.repository.UserRepository
import com.laixer.swabbr.domain.repository.VlogRepository
import com.laixer.swabbr.user
import com.laixer.swabbr.vlog
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import org.junit.Before
import org.junit.Test

class UsersVlogsUseCaseTest {

    private lateinit var usersVlogsUseCase: UsersVlogsUseCase
    private val mockUserRepository: UserRepository = mock()
    private val mockVlogRepository: VlogRepository = mock()
    private val vlogId = vlog.id
    private val userId = vlog.userId

    @Before
    fun setUp() {
        usersVlogsUseCase = UsersVlogsUseCase(mockUserRepository, mockVlogRepository)
    }

    @Test
    fun `repository get success`() {
        // given
        whenever(mockUserRepository.get(userId, false)).thenReturn(Single.just(user))
        whenever(mockVlogRepository.get(vlogId, false)).thenReturn(Single.just(vlog))
        // when
        val test = usersVlogsUseCase.get(listOf(vlogId), false).test()
        // then
        verify(mockUserRepository).get(userId, false)
        verify(mockVlogRepository).get(vlogId, false)

        test.assertNoErrors()
        test.assertComplete()
        test.assertValueCount(1)
        test.assertValue(map(listOf(user), listOf(vlog)))
    }

    @Test
    fun `repository get fail`() {
        // given
        val throwable = Throwable()
        whenever(mockVlogRepository.get(vlogId, false)).thenReturn(Single.error(throwable))
        // when
        val test = usersVlogsUseCase.get(listOf(vlogId), false).test()
        // then
        verify(mockVlogRepository).get(vlogId, false)

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
    private val vlogId = vlog.id
    private val userId = vlog.userId

    @Before
    fun setUp() {
        userVlogUseCase = UserVlogUseCase(mockUserRepository, mockVlogRepository)
    }

    @Test
    fun `repository get success`() {
        // given
        whenever(mockUserRepository.get(userId, false)).thenReturn(Single.just(user))
        whenever(mockVlogRepository.get(vlogId, false)).thenReturn(Single.just(vlog))
        // when
        val test = userVlogUseCase.get(vlogId, false).test()
        // then
        verify(mockUserRepository).get(userId, false)
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
        whenever(mockVlogRepository.get(vlogId, false)).thenReturn(Single.error(throwable))
        // when
        val test = userVlogUseCase.get(vlogId, false).test()
        // then
        verify(mockVlogRepository).get(vlogId, false)

        test.assertNoValues()
        test.assertNotComplete()
        test.assertError(throwable)
        test.assertValueCount(0)
    }
}

class UserVlogsUseCaseTest {
    private lateinit var userVlogsUseCase: UserVlogsUseCase
    private val mockVlogRepository: VlogRepository = mock {}
    private val vlogId = vlog.id
    private val userId = vlog.userId

    @Before
    fun setUp() {
        userVlogsUseCase = UserVlogsUseCase(mockVlogRepository)
    }

    @Test
    fun `repository get success`() {
        // given
        whenever(mockVlogRepository.getUserVlogs(userId, false)).thenReturn(Single.just(listOf(vlog)))
        // when
        val test = userVlogsUseCase.get(userId, false).test()
        // then
        verify(mockVlogRepository).getUserVlogs(userId, false)

        test.assertNoErrors()
        test.assertComplete()
        test.assertValueCount(1)
        test.assertValue(listOf(vlog))
    }

    @Test
    fun `repository get fail`() {
        // given
        val throwable = Throwable()
        whenever(mockVlogRepository.getUserVlogs(userId, false)).thenReturn(Single.error(throwable))
        // when
        val test = userVlogsUseCase.get(userId, false).test()
        // then
        verify(mockVlogRepository).getUserVlogs(userId, false)

        test.assertNoValues()
        test.assertNotComplete()
        test.assertError(throwable)
        test.assertValueCount(0)
    }
}
