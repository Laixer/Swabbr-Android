package com.laixer.sample.domain.usecase

import com.laixer.sample.domain.model.User
import com.laixer.sample.domain.repository.UserRepository
import io.reactivex.Single

class UsersUseCase constructor(private val userRepository: UserRepository) {

    fun get(refresh: Boolean): Single<List<User>> =
        userRepository.get(refresh)

    fun get(userId: String, refresh: Boolean): Single<User> =
        userRepository.get(userId, refresh)
}
