package com.laixer.swabbr.domain.usecase

import com.laixer.swabbr.domain.model.User
import com.laixer.swabbr.domain.repository.UserRepository
import io.reactivex.Single

class UsersUseCase constructor(private val userRepository: UserRepository) {

    fun get(userId: String, refresh: Boolean): Single<User> =
        userRepository.get(userId, refresh)

    fun search(query: String): Single<List<User>> =
        userRepository.search(query)
}
