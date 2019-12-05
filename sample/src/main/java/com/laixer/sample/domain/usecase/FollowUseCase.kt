package com.laixer.sample.domain.usecase

import com.laixer.sample.domain.repository.FollowRepository
import io.reactivex.Single

class FollowUseCase constructor(private val followRepository: FollowRepository) {

    fun getFollowStatus(targetId: String): Single<String> =
        followRepository.getFollowStatus(targetId)
}
