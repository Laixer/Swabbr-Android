package com.laixer.swabbr.domain.repository

import com.laixer.swabbr.domain.model.User
import io.reactivex.Single

interface UserRepository {

    fun get(userId: String, refresh: Boolean): Single<User>

    fun set(user: User): Single<User>

    fun search(name: String): Single<List<User>>
}
