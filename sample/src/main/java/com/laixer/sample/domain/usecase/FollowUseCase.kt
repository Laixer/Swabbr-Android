package com.laixer.sample.domain.usecase

import com.laixer.sample.domain.model.FollowRequest
import com.laixer.sample.domain.repository.FollowRepository
import io.reactivex.Single

class FollowUseCase constructor(private val followRepository: FollowRepository) {

    fun get(userId: String): Single<List<FollowRequest>> =
        followRepository.get(userId)

    fun get(receiverId: String): Single<FollowRequest> =
        followRepository.get(receiverId)

    fun requestFollow(receiverId: String) {
        followRepository.requestFollow(receiverId)
    }

    fun requestUnfollow(receiverId: String) {
        followRepository.requestUnfollow(receiverId)
    }
}
