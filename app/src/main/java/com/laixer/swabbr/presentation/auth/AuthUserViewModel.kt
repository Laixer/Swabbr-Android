package com.laixer.swabbr.presentation.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.auth0.android.jwt.JWT
import com.laixer.presentation.Resource
import com.laixer.presentation.setError
import com.laixer.presentation.setSuccess
import com.laixer.swabbr.domain.types.FollowRequestStatus
import com.laixer.swabbr.domain.usecase.AuthUserUseCase
import com.laixer.swabbr.domain.usecase.FollowUseCase
import com.laixer.swabbr.presentation.model.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.*

/**
 *  View model for displaying the currently authenticated user.
 *  This also contains some functionality for incoming follow
 *  requests. TODO Split functionality.
 */
open class AuthUserViewModel constructor(
    private val userManager: UserManager,
    private val authUserUseCase: AuthUserUseCase,
    private val followUseCase: FollowUseCase
) : ViewModel() {

    val user = MutableLiveData<Resource<UserCompleteItem>>()
    val statistics = MutableLiveData<Resource<UserWithStatsItem>>()

    // TODO FollowRequestWithUserWrapper or something similar.
    val followRequests = MutableLiveData<Resource<List<Pair<FollowRequestItem, UserItem>>>>()

    private val compositeDisposable = CompositeDisposable()

    fun getSelf(refresh: Boolean) = compositeDisposable.add(authUserUseCase
        .getSelf(refresh)
        .subscribeOn(Schedulers.io())
        .map { it.mapToPresentation() }
        .subscribe(
            { user.setSuccess(it) },
            { user.setError(it.message) }
        )
    )

    fun updateSelf(item: UserCompleteItem) {
        compositeDisposable.add(authUserUseCase
            .updateSelf(item.mapToDomain())
            .subscribeOn(Schedulers.io())
            .map { it.mapToPresentation() }
            .subscribe(
                { user.setSuccess(it) },
                { user.setError(it.message) }
            )
        )
    }

    fun getStatistics(refresh: Boolean) =
        compositeDisposable.add(authUserUseCase
            .getSelfWithStats(refresh)
            .subscribeOn(Schedulers.io())
            .map { it.mapToPresentation() }
            .subscribe(
                { statistics.setSuccess(it) },
                { statistics.setError(it.message) }
            )
        )

    fun getIncomingFollowRequests() =
        compositeDisposable.add(authUserUseCase
            .getIncomingFollowRequestsWithUsers()
            .subscribeOn(Schedulers.io())
            .map { list -> list.map { Pair(it.first.mapToPresentation(), it.second.mapToPresentation()) } }
            .subscribe(
                { followRequests.setSuccess(it) },
                { followRequests.setError(it.message) }
            )
        )

    fun acceptRequest(requesterId: UUID) = compositeDisposable.add(
        followUseCase
            .acceptRequest(requesterId)
            .subscribeOn(Schedulers.io())
            .subscribe(
                {
                    followRequests.value?.data?.let { list ->
                        followRequests.setSuccess(list.toMutableList().apply {
                            find { it.first.requesterId == requesterId }?.let { pair ->
                                remove(pair)
                                add(
                                    Pair(
                                        pair.first.apply { requestStatus = FollowRequestStatus.ACCEPTED },
                                        pair.second
                                    )
                                )
                            }
                        })
                    }
                },
                {
                    followRequests.setError(it.message)
                }
            )
    )

    fun declineRequest(requesterId: UUID) = compositeDisposable.add(
        followUseCase
            .declineRequest(requesterId)
            .subscribeOn(Schedulers.io())
            .subscribe(
                {
                    followRequests.value?.data?.let { list ->
                        followRequests.setSuccess(list.toMutableList().apply {
                            find { it.first.requesterId == requesterId }?.let { pair ->
                                remove(pair)
                                add(
                                    Pair(
                                        pair.first.apply { requestStatus = FollowRequestStatus.DECLINED },
                                        pair.second
                                    )
                                )
                            }
                        })
                    }
                },
                {
                    followRequests.setError(it.message)
                }
            )
    )

    /**
     *  Get the jwt token of the current user.
     */
    fun getAuthToken(): JWT? = userManager.token

    // TODO We might want to store this in the usermanager
    /**
     *  Get the id of the current user.
     */
    fun getAuthUserId(): UUID? = this.authUserUseCase.getSelfId()

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }

}
