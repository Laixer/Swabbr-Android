package com.laixer.swabbr.presentation.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.laixer.presentation.Resource
import com.laixer.presentation.setError
import com.laixer.presentation.setLoading
import com.laixer.presentation.setSuccess
import com.laixer.swabbr.domain.types.FollowRequestStatus
import com.laixer.swabbr.domain.usecase.AuthUserUseCase
import com.laixer.swabbr.domain.usecase.FollowUseCase
import com.laixer.swabbr.domain.usecase.UsersUseCase
import com.laixer.swabbr.domain.usecase.VlogUseCase
import com.laixer.swabbr.presentation.model.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.*

// TODO Simply save the user id once
/**
 *  View model for a user profile of any user.
 */
class ProfileViewModel constructor(
    private val authUserUseCase: AuthUserUseCase,
    private val usersUseCase: UsersUseCase,
    private val vlogUseCase: VlogUseCase,
    private val followUseCase: FollowUseCase
) : ViewModel() {
    val followingUsers = MutableLiveData<Resource<List<UserItem>>>()
    val profile = MutableLiveData<Resource<UserItem>>()
    val profileVlogs = MutableLiveData<Resource<List<VlogWrapperItem>>>()
    val followStatus = MutableLiveData<Resource<FollowRequestItem>>()
    private val compositeDisposable = CompositeDisposable()

    fun getReactionCount(vlogId: UUID) = vlogUseCase.getReactionCount(vlogId)

    /**
     *  Deletes a vlog. This is only callable on vlogs
     *  owned by the currently authenticated user.
     */
    fun deleteVlog(userId: UUID, vlogId: UUID) =
        compositeDisposable.add(vlogUseCase
            .delete(vlogId)
            .subscribe(
                { getProfileVlogs(userId) },
                { profileVlogs.setError(it.message) }
            )

        )

    fun getProfile(userId: UUID, refresh: Boolean = false) =
        compositeDisposable.add(usersUseCase
            .get(userId, refresh)
            .subscribeOn(Schedulers.io())
            .map { it.mapToPresentation() }
            .subscribe(
                { profile.setSuccess(it) },
                { profile.setError(it.message) }
            )
        )

    fun getProfileVlogs(userId: UUID, refresh: Boolean = false) =
        compositeDisposable.add(vlogUseCase
            .getAllForUser(userId, refresh)
            .doOnSubscribe { profileVlogs.setLoading() }
            .subscribeOn(Schedulers.io())
            .map { list ->
                list.sortedByDescending { it.vlog.dateStarted }
                    .map { wrapper -> wrapper.mapToPresentation() }
            }
            .subscribe(
                { profileVlogs.setSuccess(it) },
                { profileVlogs.setError(it.message) }
            )
        )

    fun getFollowing(userId: UUID, refresh: Boolean = false) = compositeDisposable.add(usersUseCase
        .getFollowing(userId, refresh)
        .doOnSubscribe { followingUsers.setLoading() }
        .subscribeOn(Schedulers.io())
        .map { it.mapToPresentation() }
        .subscribe(
            { followingUsers.setSuccess(it) },
            { followingUsers.setError(it.message) }
        )
    )

    /**
     *  Updates the status of a follow request. The requesting
     *  user id is always the currently authenticated user.
     *
     *  @param The receiving user id.
     */
    fun getFollowRequest(receiverId: UUID) =
        compositeDisposable.add(followUseCase
            .get(authUserUseCase.getSelfId(), receiverId)
            .doOnSubscribe { followStatus.setLoading() }
            .subscribeOn(Schedulers.io())
            .subscribe(
                { followStatus.setSuccess(it.mapToPresentation()) },
                { followStatus.setError(it.message) }
            )
        )

    fun sendFollowRequest(receiverId: UUID) =
        compositeDisposable.add(followUseCase
            .sendFollowRequest(receiverId)
            .doOnSubscribe { followStatus.setLoading() }
            .subscribeOn(Schedulers.io())
            .subscribe(
                {
                    // TODO Can be improved
                    // After sending the follow request, get it immediately.
                    // Some updates might have taken place automatically.
                    followStatus.setSuccess(followUseCase
                        .get(authUserUseCase.getSelfId(), receiverId)
                        .map { followRequest -> followRequest.mapToPresentation() }
                        .blockingGet())
                },
                { followStatus.setError(it.message) }
            )
        )

    fun unfollow(receiverId: UUID) =
        compositeDisposable.add(followUseCase
            .unfollow(receiverId)
            .doOnSubscribe { followStatus.setLoading() }
            .subscribeOn(Schedulers.io())
            .subscribe(
                { nonexistentFollowRequest(receiverId) },
                { followStatus.setError(it.message) }
            )
        )

    fun cancelFollowRequest(receiverId: UUID) =
        compositeDisposable.add(followUseCase
            .cancelFollowRequest(receiverId)
            .doOnSubscribe { followStatus.setLoading() }
            .subscribeOn(Schedulers.io())
            .subscribe(
                { nonexistentFollowRequest(receiverId) },
                { followStatus.setError(it.message) }
            )
        )

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }

    /**
     *  Creates a follow request entity representing a non
     *  existent follow request between the current user
     *  and a user with id [receiverId].
     *
     *  @param Follow request receiver user id.
     */
    private fun nonexistentFollowRequest(receiverId: UUID) =
        followStatus.setSuccess(
            FollowRequestItem(
                requesterId = authUserUseCase.getSelfId(),
                receiverId = receiverId,
                requestStatus = FollowRequestStatus.NONEXISTENT,
                timeCreated = null
            )
        )
}
