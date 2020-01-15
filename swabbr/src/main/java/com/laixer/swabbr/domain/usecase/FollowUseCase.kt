package com.laixer.swabbr.domain.usecase

import com.laixer.swabbr.domain.model.User
import com.laixer.swabbr.domain.repository.FollowRepository
import io.reactivex.Single

class FollowUseCase constructor(private val followRepository: FollowRepository) {

    fun getFollowStatus(userId: String): Single<Int> =
        followRepository.getFollowStatus(userId)

    fun getFollowers(userId: String): Single<List<User>> =
        followRepository.getFollowers(userId)

    fun getFollowing(userId: String): Single<List<User>> =
        followRepository.getFollowing(userId)

    fun getIncomingRequests(): Single<List<User>> =
        followRepository.getIncomingRequests()

    fun sendFollowRequest(userId: String): Single<Int> =
        followRepository.sendFollowRequest(userId)

    fun cancelFollowRequest(userId: String): Single<Int> =
        followRepository.cancelFollowRequest(userId)

    fun unfollow(userId: String): Single<Int> =
        followRepository.unfollow(userId)

    fun acceptRequest(userId: String): Single<Int> =
        followRepository.acceptRequest(userId)

    fun declineRequest(userId: String): Single<Int> =
        followRepository.declineRequest(userId)
}
