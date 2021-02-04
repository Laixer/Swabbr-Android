package com.laixer.swabbr.data.datasource.remote

import com.laixer.swabbr.data.datasource.UserDataSource
import com.laixer.swabbr.data.datasource.model.mapToData
import com.laixer.swabbr.data.datasource.model.mapToDomain
import com.laixer.swabbr.data.datasource.model.remote.UserApi
import com.laixer.swabbr.domain.model.User
import com.laixer.swabbr.domain.model.UserComplete
import com.laixer.swabbr.domain.model.UserUpdatableProperties
import com.laixer.swabbr.domain.model.UserWithStats
import com.laixer.swabbr.domain.types.Pagination
import io.reactivex.Completable
import io.reactivex.Single
import java.util.*

class UserDataSourceImpl constructor(
    val api: UserApi
) : UserDataSource {
    override fun get(userId: UUID): Single<User> = api.getUser(userId).map { it.mapToDomain() }

    override fun getWithStats(userId: UUID): Single<UserWithStats> = api.getWithStats(userId).map { it.mapToDomain() }

    override fun getSelf(): Single<UserComplete> = api.getSelf().map { it.mapToDomain() }

    override fun getSelfWithStats(): Single<UserWithStats> = api.getSelfWithStats().map { it.mapToDomain() }

    override fun search(query: String, pagination: Pagination): Single<List<User>> =
        api.search(query, pagination.sortingOrder, pagination.limit, pagination.offset).map { it.mapToDomain() }

    override fun update(user: UserUpdatableProperties): Completable = api.update(user.mapToData())

    override fun getFollowing(userId: UUID, pagination: Pagination): Single<List<User>> =
        api.getFollowing(userId, pagination.sortingOrder, pagination.limit, pagination.offset).map { it.mapToDomain() }

    override fun getFollowers(userId: UUID, pagination: Pagination): Single<List<User>> =
        api.getFollowers(userId, pagination.sortingOrder, pagination.limit, pagination.offset).map { it.mapToDomain() }
}
