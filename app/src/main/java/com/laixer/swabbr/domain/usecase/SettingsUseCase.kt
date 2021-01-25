package com.laixer.swabbr.domain.usecase

import com.laixer.swabbr.domain.model.UserComplete
import com.laixer.swabbr.domain.model.UserUpdatableProperties
import com.laixer.swabbr.domain.repository.UserRepository
import io.reactivex.Single

/**
 *  Use case for managing user settings. Note that all user settings are
 *  passed directly to the @see UserComplete object. No actual settings
 *  object exists.
 */
class SettingsUseCase constructor(
    private val userRepository: UserRepository
) {
    /**
     *  Gets a [UserUpdatableProperties] extracted from the current user.
     *
     *  @param refresh Force a data refresh. This is recommended as [true].
     */
    fun get(refresh: Boolean): Single<UserUpdatableProperties> = userRepository
        .getSelf(refresh)
        .map { it.extractUpdatableProperties() }

    /**
     *  Stores all updated user properties through the API. Any values left
     *  at [null] will not be modified. This updated and then fetches the
     *  user again so this function can return the retrieved user after modification.
     *
     *  @param The updated user properties.
     */
    fun set(updatedUser: UserUpdatableProperties): Single<UserUpdatableProperties> = userRepository
        .update(userRepository
            .getSelf(true)
            .map { self -> updatedUser.convertToUser(self) }
            .blockingGet() // TODO Probably incorrect usage of rxjava
        )
        .andThen(userRepository
            .getSelf()
            .map { self -> self.extractUpdatableProperties() }
        )

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

    /**
     *  Extracts a [UserUpdatableProperties] object from a [UserComplete] object.
     */
    private fun UserComplete.extractUpdatableProperties(): UserUpdatableProperties = UserUpdatableProperties(
        firstName,
        lastName,
        gender,
        country,
        birthDate,
        timeZone,
        nickname,
        profileImage,
        latitude,
        longitude,
        isPrivate,
        dailyVlogRequestLimit,
        followMode
    )
}
