package com.laixer.swabbr.data.datasource.remote

import com.laixer.swabbr.data.datasource.UserRemoteDataSource
import com.laixer.swabbr.datasource.model.mapToDomain
import com.laixer.swabbr.datasource.model.remote.UsersApi
import com.laixer.swabbr.domain.model.User
import io.reactivex.Single
import java.util.UUID

class UserRemoteDataSourceImpl constructor(
    val api: UsersApi
) : UserRemoteDataSource {
    override fun get(userId: UUID): Single<User> = api.getUser(userId).map { it.mapToDomain() }

    override fun search(name: String, page: Int, itemsPerPage: Int): Single<List<User>> = api
        .searchUserByFirstname(name, page, itemsPerPage)
        .map { it.mapToDomain() }
}
