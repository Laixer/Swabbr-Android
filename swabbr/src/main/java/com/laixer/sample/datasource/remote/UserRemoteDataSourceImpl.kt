package com.laixer.swabbr.datasource.remote

import com.laixer.swabbr.data.datasource.UserRemoteDataSource
import com.laixer.swabbr.datasource.model.mapToDomain
import com.laixer.swabbr.domain.model.User
import io.reactivex.Single

class UserRemoteDataSourceImpl constructor(
    private val api: UsersApi
) : UserRemoteDataSource {

    override fun get(): Single<List<User>> =
        api.getUsers()
            .map { it.mapToDomain() }

    override fun get(userId: String): Single<User> =
        api.getUser(userId)
            .map { it.mapToDomain() }
}
