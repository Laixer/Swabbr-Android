package com.laixer.swabbr.domain.repository

import com.laixer.swabbr.domain.model.User
import io.reactivex.Single
import java.util.UUID

interface UserRepository {

    fun get(userId: UUID, refresh: Boolean): Single<User>

    fun set(user: User): Single<User>

    fun search(name: String, page: Int = 1, itemsPerPage: Int = 50): Single<List<User>>
}
