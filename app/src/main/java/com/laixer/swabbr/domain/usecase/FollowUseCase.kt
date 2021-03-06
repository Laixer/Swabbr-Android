package com.laixer.swabbr.domain.usecase

import com.laixer.swabbr.domain.interfaces.FollowRequestRepository
import com.laixer.swabbr.domain.interfaces.UserRepository
import com.laixer.swabbr.domain.model.FollowRequest
import com.laixer.swabbr.domain.model.User
import com.laixer.swabbr.domain.model.UserWithRelation
import com.laixer.swabbr.domain.types.Pagination
import io.reactivex.Completable
import io.reactivex.Single
import java.util.*

/**
 *  Use case for anything follow request related. This includes the actual
 *  user objects of the users which are following some other user.
 */
class FollowUseCase constructor(
    private val followRequestRepository: FollowRequestRepository,
    private val userRepository: UserRepository
) {
    fun get(requesterId: UUID, receiverId: UUID): Single<FollowRequest> =
        followRequestRepository.get(requesterId, receiverId)

    fun getIncomingRequests(): Single<List<FollowRequest>> = followRequestRepository.getIncomingRequests()

    fun getOutgoingRequests(): Single<List<FollowRequest>> = followRequestRepository.getOutgoingRequests()

    fun sendFollowRequest(userId: UUID): Completable = followRequestRepository.sendFollowRequest(userId)

    fun cancelFollowRequest(userId: UUID): Completable = followRequestRepository.cancelFollowRequest(userId)

    fun unfollow(userId: UUID): Completable = followRequestRepository.unfollow(userId)

    fun acceptRequest(userId: UUID): Completable = followRequestRepository.acceptRequest(userId)

    fun declineRequest(userId: UUID): Completable = followRequestRepository.declineRequest(userId)

    // TODO Pass refresh
    fun getFollowers(userId: UUID, refresh: Boolean = false): Single<List<User>> =
        userRepository.getFollowers(userId, forceRefresh = refresh)

    // TODO Pass refresh
    fun getFollowing(
        userId: UUID,
        pagination: Pagination = Pagination.default(),
        refresh: Boolean = false
    ): Single<List<User>> =
        userRepository.getFollowing(userId, forceRefresh = refresh)

    // TODO Pass refresh
    fun getFollowRequestingUsers(
        pagination: Pagination = Pagination.default(),
        refresh: Boolean = false
    )
        : Single<List<UserWithRelation>> = userRepository.getFollowRequestingUsers(pagination)
}
