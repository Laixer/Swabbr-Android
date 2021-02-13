package com.laixer.swabbr.domain.usecase

import com.laixer.swabbr.domain.interfaces.UserRepository
import com.laixer.swabbr.domain.model.User
import com.laixer.swabbr.domain.model.UserWithRelation
import com.laixer.swabbr.domain.model.UserWithStats
import com.laixer.swabbr.domain.types.Pagination
import io.reactivex.Single
import java.util.*

/**
 *  Use case with functionality with regards to other users.
 */
class UsersUseCase constructor(private val userRepository: UserRepository) {
    /**
     *  Get a user based on its id.
     *
     *  @param userId The user to get.
     *  @param refresh Force a data refresh.
     */
    fun get(userId: UUID, refresh: Boolean): Single<User> = userRepository.get(userId, refresh)

    /**
     *  Get a user and its statistics based on its id.
     *
     *  @param userId The user to get.
     *  @param refresh Force a data refresh.
     */
    fun getWithStats(userId: UUID, refresh: Boolean): Single<UserWithStats> =
        userRepository.getWithStats(userId, refresh)

    /**
     *  Search for users in our data store.
     *
     *  @param query Search query, can't be empty.
     *  @param pagination Result set control.
     */
    fun search(query: String, pagination: Pagination = Pagination.latest()): Single<List<UserWithRelation>> =
        userRepository.search(query, pagination)
}
