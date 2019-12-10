package com.laixer.sample.domain.usecase

import com.laixer.sample.domain.model.User
import com.laixer.sample.domain.repository.FollowRepository
import io.reactivex.Single

class FollowUseCase constructor(private val followRepository: FollowRepository) {

    fun getFollowStatus(userId: String): Single<String> =
        followRepository.getFollowStatus(userId)

    fun getFollowers(userId: String): Single<List<User>> =
        followRepository.getFollowers(userId)

    fun getFollowing(userId: String): Single<List<User>> =
        followRepository.getFollowing(userId)

    fun getIncomingRequests(): Single<List<User>> =
        followRepository.getIncomingRequests()

    fun sendFollowRequest(userId: String): Single<String> =
        followRepository.sendFollowRequest(userId)

    fun cancelFollowRequest(userId: String): Single<String> =
        followRepository.cancelFollowRequest(userId)

    fun unfollow(userId: String): Single<String> =
        followRepository.unfollow(userId)

    fun acceptRequest(userId: String): Single<String> =
        followRepository.acceptRequest(userId)

    fun declineRequest(userId: String): Single<String> =
        followRepository.declineRequest(userId)
}
