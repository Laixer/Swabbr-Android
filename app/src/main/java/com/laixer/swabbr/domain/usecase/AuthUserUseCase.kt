package com.laixer.swabbr.domain.usecase

import com.laixer.swabbr.domain.model.*
import com.laixer.swabbr.domain.repository.AuthRepository
import com.laixer.swabbr.domain.repository.UserRepository
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.toFlowable
import io.reactivex.schedulers.Schedulers

class AuthUserUseCase constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) {

    fun getSelf(refresh: Boolean): Single<AuthUser> = authRepository.getAuthenticatedUser(refresh)

    fun getStatistics(refresh: Boolean) = authRepository.getStatistics(refresh)

    fun getIncomingFollowRequestsWithUser(): Single<List<Pair<FollowRequest, User>>> =
        authRepository.getIncomingFollowRequests()
            .flattenAsObservable { followRequests -> followRequests }
            .flatMap { request ->
                userRepository.get(request.requesterId, true).map { user ->
                    Pair(request, user)
                }.toObservable()
            }.toList()


}


