package com.laixer.swabbr.domain.usecase

import com.laixer.swabbr.domain.model.FollowRequest
import com.laixer.swabbr.domain.model.FollowStatus
import com.laixer.swabbr.domain.model.User
import com.laixer.swabbr.domain.repository.FollowRepository
import io.reactivex.Completable
import io.reactivex.Single
import java.util.UUID

class FollowUseCase constructor(private val followRepository: FollowRepository) {

    fun getFollowStatus(userId: UUID): Single<FollowStatus> = followRepository.getFollowStatus(userId)

    fun getIncomingRequests(): Single<List<FollowRequest>> = followRepository.getIncomingRequests()

    fun getOutgoingRequests(): Single<List<FollowRequest>> = followRepository.getOutgoingRequests()

    fun sendFollowRequest(userId: UUID): Single<FollowRequest> = followRepository.sendFollowRequest(userId)

    fun cancelFollowRequest(userId: UUID): Completable = followRepository.cancelFollowRequest(userId)

    fun unfollow(userId: UUID): Completable = followRepository.unfollow(userId)

    fun acceptRequest(userId: UUID): Single<FollowRequest> = followRepository.acceptRequest(userId)

    fun declineRequest(userId: UUID): Single<FollowRequest> = followRepository.declineRequest(userId)

    fun getFollowers(userId: UUID, refresh: Boolean = false): Single<List<User>> = followRepository.getFollowers(userId, refresh)

    fun getFollowing(userId: UUID, refresh: Boolean = false): Single<List<User>> = followRepository.getFollowing(userId, refresh)

}
