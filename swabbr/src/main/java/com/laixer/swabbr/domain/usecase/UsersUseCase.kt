package com.laixer.swabbr.domain.usecase

import com.laixer.swabbr.domain.model.User
import com.laixer.swabbr.domain.repository.UserRepository
import io.reactivex.Single

class UsersUseCase constructor(private val userRepository: UserRepository) {

    fun get(refresh: Boolean): Single<List<User>> =
        userRepository.get(refresh)

    fun get(userId: String, refresh: Boolean): Single<User> =
        userRepository.get(userId, refresh)

    fun searchUser(userId: String): Single<User> =
        userRepository.searchUser(userId)
}
