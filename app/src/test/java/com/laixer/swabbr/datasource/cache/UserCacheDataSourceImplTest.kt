package com.laixer.swabbr.datasource.cache

import com.laixer.cache.ReactiveCache
import com.laixer.swabbr.Models
import com.laixer.swabbr.data.datasource.cache.UserCacheDataSourceImpl
import com.laixer.swabbr.domain.model.User
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import org.junit.Before
import org.junit.Test

class UserCacheDataSourceImplTest {

    private lateinit var dataSource: UserCacheDataSourceImpl
    private val self_key by lazy { dataSource.self_key }

    private val mockCache: ReactiveCache<User> = mock()

    private val userId = Models.user.id
    private val userIdString = userId.toString()
    private val model = Models.user

    private val list = listOf(model)
    private val throwable = Throwable()

    @Before
    fun setUp() {
        dataSource = UserCacheDataSourceImpl(mockCache)
    }

//    @Test
//    fun `get users cache success`() {
//        // given
//        whenever(mockCache.load(key)).thenReturn(Single.just(cacheList))
//        // when
//        val test = dataSource.get().test()
//        // then
//        verify(mockCache).load(key)
//        test.assertValue(cacheList)
//    }

//    @Test
//    fun `get users cache fail`() {
//        // given
//        whenever(mockCache.load(key)).thenReturn(Single.error(throwable))
//        // when
//        val test = dataSource.get().test()
//        // then
//        verify(mockCache).load(key)
//        test.assertError(throwable)
//    }

    @Test
    fun `get user cache success`() {
        // given
        whenever(mockCache.load(userIdString)).thenReturn(Single.just(model))
        // when
        val test = dataSource.get(userId).test()
        // then
        verify(mockCache).load(userIdString)
        test.assertValue(model)
    }

    @Test
    fun `get user cache fail`() {
        // given
        whenever(mockCache.load(userIdString)).thenReturn(Single.error(throwable))
        // when
        val test = dataSource.get(userId).test()
        // then
        verify(mockCache).load(userIdString)
        test.assertError(throwable)
    }

    @Test
    fun `set users cache success`() {
        // given
        whenever(mockCache.save(userIdString, model)).thenReturn(Single.just(model))
        // when
        val test = dataSource.set(list).test()
        // then
        verify(mockCache).save(userIdString, model)
        test.assertValue(list)
    }

    @Test
    fun `set users cache fail`() {
        // given
        whenever(mockCache.save(userIdString, model)).thenReturn(Single.error(throwable))
        // when
        val test = dataSource.set(list).test()
        // then
        verify(mockCache).save(userIdString, model)
        test.assertError(throwable)
    }

    @Test
    fun `set user cache success`() {
        // given
        whenever(mockCache.load(userIdString)).thenReturn(Single.error(throwable))
        whenever(mockCache.save(userIdString, model)).thenReturn(Single.just(model))
        // when
        val test = dataSource.set(list).test()
        // then
        verify(mockCache).save(userIdString, model)
        test.assertValue(list)
    }

    @Test
    fun `set user cache fail`() {
        // given
        whenever(mockCache.load(userIdString)).thenReturn(Single.error(throwable))
        whenever(mockCache.save(userIdString, model)).thenReturn(Single.error(throwable))
        // when
        val test = dataSource.set(list).test()
        // then
        verify(mockCache).save(userIdString, model)
        test.assertError(throwable)
    }
}
