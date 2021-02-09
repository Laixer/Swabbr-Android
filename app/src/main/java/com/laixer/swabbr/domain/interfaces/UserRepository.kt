package com.laixer.swabbr.domain.interfaces

import com.laixer.swabbr.domain.model.User
import com.laixer.swabbr.domain.model.UserComplete
import com.laixer.swabbr.domain.model.UserUpdatableProperties
import com.laixer.swabbr.domain.model.UserWithStats
import com.laixer.swabbr.domain.types.Pagination
import io.reactivex.Completable
import io.reactivex.Single
import java.util.*

/**
 *  Interface for a user repository. This is also capable of
 *  retrieving user statistics and personal details.
 *
 *  The forceRefresh parameter can be used to bypass any caching
 *  if an implementation uses caching. This can be desirable if
 *  we have to guarantee the retrieval of the most up-to-date
 *  information.
 */
interface UserRepository {

    /**
     *  Gets a user from our data store.
     *
     *  @param userId The user id to retrieve.
     *  @param forceRefresh Force a cache update if any caching is used.
     */
    fun get(userId: UUID, forceRefresh: Boolean = false): Single<User>

    /**
     *  Gets a user with its statistics from our data store.
     *
     *  @param userId The user id to retrieve.
     *  @param forceRefresh Force a cache update if any caching is used.
     */
    fun getWithStats(userId: UUID, forceRefresh: Boolean = false): Single<UserWithStats>

    /**
     *  Gets the currently authenticated user from our data store.
     *
     *  @param forceRefresh Force a cache update if any caching is used.
     */
    fun getSelf(forceRefresh: Boolean = false): Single<UserComplete>

    /**
     *  Gets the currently authenticated user with stats from our data store.
     *
     *  @param forceRefresh Force a cache update if any caching is used.
     */
    fun getSelfWithStats(forceRefresh: Boolean = false): Single<UserWithStats>

    /**
     *  Get all users that a user is following itself.
     *
     *  @param userId The follow request requesting user.
     *  @param pagination Controls the result set.
     *  @param forceRefresh Force a cache update if any caching is used.
     */
    fun getFollowing(userId: UUID,
                     pagination: Pagination = Pagination.latest(),
                     forceRefresh: Boolean = false): Single<List<User>>

    /**
     *  Get all users that are following a given user.
     *
     *  @param userId The follow request receiving user.
     *  @param pagination Controls the result set.
     *  @param forceRefresh Force a cache update if any caching is used.
     */
    fun getFollowers(userId: UUID,
                     pagination: Pagination = Pagination.latest(),
                     forceRefresh: Boolean = false): Single<List<User>>

    /**
     *  Search for users in our data store.
     *
     *  @param query Search query, can't be empty.
     *  @param pagination Controls the result set.
     */
    fun search(query: String, pagination: Pagination = Pagination.latest()): Single<List<User>>

    /**
     *  Update the currently authenticated user.
     *
     *  @param user The user with updated properties.
     */
    fun update(user: UserUpdatableProperties): Completable
}
