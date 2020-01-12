package com.laixer.swabbr.data.datasource

import com.laixer.swabbr.domain.model.User
import io.reactivex.Single

interface UserCacheDataSource {

    fun get(): Single<List<User>>

    fun set(item: User): Single<User>

    fun get(userId: String): Single<User>

    fun set(list: List<User>): Single<List<User>>
}

interface UserRemoteDataSource {

    fun get(): Single<List<User>>

    fun get(userId: String): Single<User>

    fun searchUser(userId: String): Single<User>
}
