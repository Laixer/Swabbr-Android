package com.laixer.swabbr.data.datasource.remote

import com.laixer.swabbr.data.datasource.UserRemoteDataSource
import com.laixer.swabbr.datasource.model.mapToDomain
import com.laixer.swabbr.datasource.model.remote.UsersApi
import com.laixer.swabbr.domain.model.User
import com.laixer.swabbr.domain.model.UserStatistics
import io.reactivex.Single
import java.util.*

class UserRemoteDataSourceImpl constructor(
    val api: UsersApi
) : UserRemoteDataSource {
    override fun get(userId: UUID): Single<User> = api.getUser(userId).map { it.mapToDomain() }

    override fun getSelf(): Single<User> = api.self().map { it.mapToDomain() }

    override fun search(query: String?, page: Int, itemsPerPage: Int): Single<List<User>> = api
        .search(query, page, itemsPerPage)
        .map { it.mapToDomain() }
}
