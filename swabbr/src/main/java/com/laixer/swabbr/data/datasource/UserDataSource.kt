package com.laixer.swabbr.data.datasource

import com.laixer.swabbr.domain.model.User
import io.reactivex.Single
import java.util.UUID

interface UserCacheDataSource {

    val key: String
        get() = "USERS"

    fun set(item: User): Single<User>

    fun get(userId: UUID): Single<User>

    fun set(list: List<User>): Single<List<User>>
}

interface UserRemoteDataSource {

    fun get(userId: UUID): Single<User>

    fun search(name: String, page: Int = 1, itemsPerPage: Int = 50): Single<List<User>>
}
