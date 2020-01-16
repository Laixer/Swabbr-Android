package com.laixer.swabbr.domain.usecase

import com.laixer.swabbr.domain.model.FollowRequest
import com.laixer.swabbr.domain.model.User
import com.laixer.swabbr.domain.repository.FollowRepository
import io.reactivex.Single

class FollowUseCase constructor(private val followRepository: FollowRepository) {

    fun getFollowRequest(userId: String): Single<FollowRequest> =
        followRepository.getFollowStatus(userId)

    fun getFollowers(userId: String): Single<List<User>> =
        followRepository.getFollowers(userId)

    fun getFollowing(userId: String): Single<List<User>> =
        followRepository.getFollowing(userId)

    fun getIncomingRequests(): Single<List<User>> =
        followRepository.getIncomingRequests()

    fun sendFollowRequest(userId: String): Single<FollowRequest> =
        followRepository.sendFollowRequest(userId)

    fun cancelFollowRequest(userId: String): Single<FollowRequest> =
        followRepository.cancelFollowRequest(userId)

    fun unfollow(userId: String): Single<FollowRequest> =
        followRepository.unfollow(userId)

    fun acceptRequest(userId: String): Single<FollowRequest> =
        followRepository.acceptRequest(userId)

    fun declineRequest(userId: String): Single<FollowRequest> =
        followRepository.declineRequest(userId)
}
