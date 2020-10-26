package com.laixer.swabbr.presentation.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.auth0.android.jwt.JWT
import com.laixer.presentation.Resource
import com.laixer.presentation.setError
import com.laixer.presentation.setSuccess
import com.laixer.swabbr.domain.usecase.AuthUserUseCase
import com.laixer.swabbr.domain.usecase.FollowUseCase
import com.laixer.swabbr.presentation.model.FollowRequestItem
import com.laixer.swabbr.presentation.model.UserItem
import com.laixer.swabbr.presentation.model.UserStatisticsItem
import com.laixer.swabbr.presentation.model.mapToPresentation
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.*

open class AuthUserViewModel constructor(
    private val userManager: UserManager,
    private val authUserUseCase: AuthUserUseCase,
    private val followUseCase: FollowUseCase
) : ViewModel() {

    val user = MutableLiveData<Resource<UserItem>>()
    val statistics = MutableLiveData<Resource<UserStatisticsItem>>()
    val followRequests = MutableLiveData<Resource<List<Pair<FollowRequestItem, UserItem>>>>()

    private val compositeDisposable = CompositeDisposable()

    fun getSelf(refresh: Boolean) = compositeDisposable.add(authUserUseCase
        .getSelf(refresh)
        .subscribeOn(Schedulers.io())
        .map { it.mapToPresentation() }
        .subscribe(
            { user.setSuccess(it.user) },
            { user.setError(it.message) }
        )
    )

    fun getStatistics(refresh: Boolean) =
        compositeDisposable.add(authUserUseCase
            .getStatistics(refresh)
            .subscribeOn(Schedulers.io())
            .map { it.mapToPresentation() }
            .subscribe(
                { statistics.setSuccess(it) },
                { statistics.setError(it.message) }
            )
        )

    fun getIncomingFollowRequests() =
        compositeDisposable.add(authUserUseCase
            .getIncomingFollowRequestsWithUser()
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
            .map { it.mapToPresentation() }
            .subscribe(
                { request ->
                    followRequests.value?.data?.let { list ->
                        followRequests.setSuccess(list.toMutableList().apply {
                            find { it.first.requesterId == requesterId }?.let {
                                remove(it)
                                add(Pair(request, it.second))
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
            .map { it.mapToPresentation() }
            .subscribe(
                { request ->
                    followRequests.value?.data?.let { list ->
                        followRequests.setSuccess(list.toMutableList().apply {
                            find { it.first.requesterId == requesterId }?.let {
                                remove(it)
                                add(Pair(request, it.second))
                            }
                        })
                    }
                },
                {
                    followRequests.setError(it.message)
                }
            )
    )


    fun getAuthToken(): JWT? = userManager.token
    fun getAuthUserId(): UUID? = userManager.getUserProperty("id")?.let(UUID::fromString)

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }

}
