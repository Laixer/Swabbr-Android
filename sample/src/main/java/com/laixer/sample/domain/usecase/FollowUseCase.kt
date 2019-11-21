package com.laixer.sample.domain.usecase

import com.laixer.sample.domain.model.FollowRequest
import com.laixer.sample.domain.repository.FollowRepository
import io.reactivex.Single

class FollowUseCase constructor(private val followRepository: FollowRepository) {

    fun get(receiverId: String): Single<List<FollowRequest>> =
        followRepository.get(receiverId)

    fun get(requesterId: String, receiverId: String): Single<FollowRequest> =
        followRepository.get(requesterId, receiverId)
}
