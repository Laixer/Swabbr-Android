package com.laixer.sample.domain.usecase

import com.laixer.sample.domain.model.User
import com.laixer.sample.domain.repository.FollowRepository
import io.reactivex.Single

class FollowUseCase constructor(private val followRepository: FollowRepository) {

    fun getFollowStatus(targetId: String): Single<String> =
        followRepository.getFollowStatus(targetId)

    fun getFollowers(targetId: String): Single<List<User>> =
        followRepository.getFollowers(targetId)

    fun getFollowing(targetId: String): Single<List<User>> =
        followRepository.getFollowing(targetId)

//    fun getIncomingFollowRequests(): Single<List<User>> =
//        ""

//    fun sendFollowRequest(targetId: String) {
//        //followRepository
//    }
}
