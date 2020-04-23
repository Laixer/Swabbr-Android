package com.laixer.swabbr.domain.usecase

import com.laixer.swabbr.domain.model.FollowRequest
import com.laixer.swabbr.domain.model.User
import com.laixer.swabbr.domain.repository.FollowRepository
import io.reactivex.Single
import java.util.UUID

class FollowUseCase constructor(private val followRepository: FollowRepository) {

    fun getFollowRequest(userId: UUID): Single<FollowRequest> = followRepository.getFollowStatus(userId)

    fun getFollowers(userId: UUID): Single<List<User>> = followRepository.getFollowers(userId)

    fun getFollowing(userId: UUID): Single<List<User>> = followRepository.getFollowing(userId)

    fun getIncomingRequests(): Single<List<User>> = followRepository.getIncomingRequests()

    fun sendFollowRequest(userId: UUID): Single<FollowRequest> = followRepository.sendFollowRequest(userId)

    fun cancelFollowRequest(userId: UUID): Single<FollowRequest> = followRepository.cancelFollowRequest(userId)

    fun unfollow(userId: UUID): Single<FollowRequest> = followRepository.unfollow(userId)

    fun acceptRequest(userId: UUID): Single<FollowRequest> = followRepository.acceptRequest(userId)

    fun declineRequest(userId: UUID): Single<FollowRequest> = followRepository.declineRequest(userId)
}
