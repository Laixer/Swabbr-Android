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

    /**
     *  Updates the user based on [UserUpdatablePropertiesItem]
     *  and then gets the user from the data store. Note that
     *  the result is assigned to [user]. Observe this resource
     *  to be notified of any changes.
     *
     *  Leave any properties that should not be modified as null.
     *
     *  @param user User with updated properties.
     */
    fun updateGetSelf(user: UserUpdatablePropertiesItem) {
        compositeDisposable.add(authUserUseCase
            .updateSelf(user.mapToDomain())
            .andThen(authUserUseCase.getSelf(true)
                .map { it.mapToPresentation() }
            )
            .subscribeOn(Schedulers.io())
            .subscribe(
                {
                    this.user.setSuccess(it)
                },
                { this.user.setError(it.message) }
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

    /**
     *  Accepts an incoming follow request for the current user.
     *  Note that this also bumps the follower count by one. This
     *  is done locally, no data is fetched.
     *
     *  @param requesterId The user that wants to follow us.
     */
    fun acceptRequest(requesterId: UUID) {
        // First bump for instant UI update
        if (statistics.value?.data != null) {
            val newTotalFollowers = statistics.value!!.data!!.totalFollowers + 1
            statistics.value!!.data!!.totalFollowers = newTotalFollowers
            statistics.setSuccess(statistics.value!!.data!!)
        }

        // Then send
        compositeDisposable.add(
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
    }

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
    fun getSelfId(): UUID = this.authUserUseCase.getSelfId()

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }

}
