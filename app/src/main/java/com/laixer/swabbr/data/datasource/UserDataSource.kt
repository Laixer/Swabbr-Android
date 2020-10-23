package com.laixer.swabbr.data.datasource

import com.laixer.swabbr.domain.model.User
import com.laixer.swabbr.domain.model.UserStatistics
import io.reactivex.Single
import java.util.UUID

interface UserCacheDataSource {

    val key: String
        get() = "users"

    fun add(user: User): Single<User>

    fun get(userId: UUID): Single<User>

    fun get(): Single<List<User>>

    fun set(list: List<User>): Single<List<User>>
}

interface UserRemoteDataSource {

    fun get(userId: UUID): Single<User>

    fun search(query: String?, page: Int = 1, itemsPerPage: Int = 50): Single<List<User>>

    fun getFollowing(userId: UUID): Single<List<User>>
}
