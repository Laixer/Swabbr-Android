package com.laixer.sample.datasource.remote

import com.laixer.sample.data.datasource.UserRemoteDataSource
import com.laixer.sample.datasource.model.mapToDomain
import com.laixer.sample.domain.model.User
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
