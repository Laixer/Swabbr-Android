package com.laixer.swabbr.domain.usecase

import com.laixer.swabbr.domain.model.*
import com.laixer.swabbr.domain.interfaces.FollowRequestRepository
import com.laixer.swabbr.domain.interfaces.UserRepository
import com.laixer.swabbr.services.users.UserManager
import io.reactivex.Completable
import io.reactivex.Single
import java.util.*

/**
 *  Use case with regards to the currently authenticated user.
 *  This is responsible for operations on said user.
 */
class AuthUserUseCase constructor(
    private val userRepository: UserRepository,
    private val userManager: UserManager
) {
    /**
     *  Gets the id of the currently authenticated user. Only call this if
     *  we are authenticated.
     */
    fun getSelfId(): UUID = userManager.getUserIdOrNull() ?: UUID.randomUUID() // TODO Horrible solution

    /**
     *  Get the currently authenticated user. This contains
     *  personal details as well.
     *
     *  @param refresh Force a data refresh.
     */
    fun getSelf(refresh: Boolean): Single<UserComplete> = userRepository.getSelf(refresh)

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
