package com.laixer.swabbr.data.datasource

import com.laixer.swabbr.domain.model.User
import com.laixer.swabbr.domain.model.UserStatistics
import io.reactivex.Single
import java.util.UUID

interface UserCacheDataSource {

    val self_key: String
        get() = "self"

    fun set(item: User): Single<User>

    fun get(userId: UUID): Single<User>

    fun getAll(): Single<List<User>>

    fun set(list: List<User>): Single<List<User>>

    fun getSelf(): Single<User>
}

interface UserRemoteDataSource {

    fun get(userId: UUID): Single<User>

    fun search(query: String?, page: Int = 1, itemsPerPage: Int = 50): Single<List<User>>
}
