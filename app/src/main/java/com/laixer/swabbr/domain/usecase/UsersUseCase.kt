package com.laixer.swabbr.domain.usecase

import com.laixer.swabbr.domain.model.User
import com.laixer.swabbr.domain.repository.UserRepository
import io.reactivex.Single
import java.util.UUID

class UsersUseCase constructor(private val userRepository: UserRepository) {

    fun get(userId: UUID, refresh: Boolean): Single<User> = userRepository.get(userId, refresh)

    fun search(query: String?, page: Int = 1, itemsPerPage: Int = 50): Single<List<User>> =
        userRepository.search(query, page, itemsPerPage)

    fun getFollowing(userId: UUID, refresh: Boolean): Single<List<User>> =
        userRepository.getFollowing(userId, refresh)
}
