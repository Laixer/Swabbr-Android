package com.laixer.swabbr.data.datasource

import com.laixer.swabbr.data.api.UserApi
import com.laixer.swabbr.data.interfaces.UserDataSource
import com.laixer.swabbr.data.model.mapToData
import com.laixer.swabbr.data.model.mapToDomain
import com.laixer.swabbr.domain.model.*
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

    override fun getFollowing(userId: UUID, pagination: Pagination): Single<List<User>> =
        api.getFollowing(userId, pagination.sortingOrder, pagination.limit, pagination.offset).map { it.mapToDomain() }

    override fun getFollowers(userId: UUID, pagination: Pagination): Single<List<User>> =
        api.getFollowers(userId, pagination.sortingOrder, pagination.limit, pagination.offset).map { it.mapToDomain() }

    override fun getFollowRequestingUsers(pagination: Pagination): Single<List<UserWithRelation>> =
        api.getFollowRequestingUsers(pagination.sortingOrder, pagination.limit, pagination.offset)
            .map { it.mapToDomain() }

    override fun getVlogLikingUsers(pagination: Pagination): Single<List<LikingUserWrapper>> =
        api.getVlogLikingUsers(pagination.sortingOrder, pagination.limit, pagination.offset).map { it.mapToDomain() }

    override fun search(query: String, pagination: Pagination): Single<List<UserWithRelation>> =
        api.search(query, pagination.sortingOrder, pagination.limit, pagination.offset).map { it.mapToDomain() }

    override fun update(user: UserUpdatableProperties): Completable = api.update(user.mapToData())
}
