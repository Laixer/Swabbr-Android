package com.laixer.swabbr.domain.repository

import com.laixer.swabbr.domain.model.User
import io.reactivex.Single

interface UserRepository {

    fun get(refresh: Boolean): Single<List<User>>

    fun get(userId: String, refresh: Boolean): Single<User>
}
