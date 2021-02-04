package com.laixer.swabbr.domain.usecase

import com.laixer.swabbr.domain.model.*
import com.laixer.swabbr.domain.repository.FollowRequestRepository
import com.laixer.swabbr.domain.repository.UserRepository
import io.reactivex.Completable
import io.reactivex.Single
import java.util.*

// TODO Look at this naming due to inconsistency with domain and data.
/**
 *  Use case with regards to the currently authenticated user.
 *  This is responsible for operations on said user.
 */
class AuthUserUseCase constructor(
    private val userRepository: UserRepository,
    private val followRequestRepository: FollowRequestRepository
) {
    // TODO This seems suboptimal
    /**
     *  Gets the id of the currently authenticated user.
     */
    fun getSelfId(): UUID = getSelf(false)
        .blockingGet()
        .id

    /**
     *  Get the currently authenticated user. This contains
     *  personal details as well.
     *
     *  @param refresh Force a data refresh.
     */
    fun getSelf(refresh: Boolean): Single<UserComplete> = userRepository.getSelf(refresh)

    /**
     *  Get the currently authenticated user with statistics,
     *  without any personal details.
     *
     *  @param refresh Force a data refresh.
     */
    fun getSelfWithStats(refresh: Boolean): Single<UserWithStats> = userRepository.getSelfWithStats(refresh)

    // TODO Maybe move this functionality to the Backend?
    /**
     *  Get all incoming follow requests for the currently
     *  authenticated user.
     */
    fun getIncomingFollowRequestsWithUsers(): Single<List<Pair<FollowRequest, User>>> =
        followRequestRepository.getIncomingRequests()
            .flattenAsObservable { followRequests -> followRequests }
            .flatMap { request ->
                userRepository.get(request.requesterId, true).map { user ->
                    Pair(request, user)
                }.toObservable()
            }.toList()

    /**
     *  Update the currently authenticated user.
     *
     *  @param user User with updated properties.
     */
    fun updateSelf(user: UserUpdatableProperties): Completable = userRepository.update(user)

    // TODO Do we need this?
    /**
     *  Converts a [UserUpdatableProperties] object to a [UserComplete] object.
     *  All properties which are left as [null] will not be assigned.
     */
    private fun UserUpdatableProperties.convertToUser(current: UserComplete): UserComplete = UserComplete(
        current.id,
        this.firstName ?: current.firstName,
        this.lastName ?: current.lastName,
        this.gender ?: current.gender,
        this.country ?: current.country,
        this.birthDate ?: current.birthDate,
        this.timeZone ?: current.timeZone,
        this.nickname ?: current.nickname,
        this.profileImage ?: current.profileImage,
        this.latitude ?: current.latitude,
        this.longitude ?: current.longitude,
        this.isPrivate ?: current.isPrivate,
        this.dailyVlogRequestLimit ?: current.dailyVlogRequestLimit,
        this.followMode ?: current.followMode
    )
}
