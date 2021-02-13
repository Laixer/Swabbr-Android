package com.laixer.swabbr.presentation.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.laixer.presentation.Resource
import com.laixer.presentation.setError
import com.laixer.presentation.setLoading
import com.laixer.presentation.setSuccess
import com.laixer.swabbr.domain.model.Vlog
import com.laixer.swabbr.domain.types.FollowRequestStatus
import com.laixer.swabbr.domain.usecase.AuthUserUseCase
import com.laixer.swabbr.domain.usecase.FollowUseCase
import com.laixer.swabbr.domain.usecase.UsersUseCase
import com.laixer.swabbr.domain.usecase.VlogUseCase
import com.laixer.swabbr.presentation.model.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.*

/**
 *  View model for a user profile of any user.
 */
class ProfileViewModel constructor(
    private val authUserUseCase: AuthUserUseCase,
    private val usersUseCase: UsersUseCase,
    private val vlogUseCase: VlogUseCase,
    private val followUseCase: FollowUseCase
) : ViewModel() {
    /**
     *  Resource in which the user we look at is stored, along
     *  with statistics about the user.
     */
    val user = MutableLiveData<Resource<UserWithStatsItem>>()

    /**
     *  Resource which stores vlogs owned by [user].
     */
    val userVlogs = MutableLiveData<Resource<List<VlogWrapperItem>>>()

    /**
     *  Users that the [user] is following himself.
     */
    val followingUsers = MutableLiveData<Resource<List<UserItem>>>()

    /**
     *  Follower base of the [user]. This also contains all incoming
     *  follow requests with their corresponding users. These follow
     *  requesting users will always be first in the list. This is only
     *  relevant when displaying the profile of the current user, else
     *  this list will contain no follow requesting [UserWithRelationItem]s.
     *
     *  Note that this contains a list of [UserWithRelationItem]. The
     *  followers [UserItem]s will be converted to this format so we
     *  can merge these two types of data together in one list.
     */
    val followersAndFollowRequestingUsers = MutableLiveData<Resource<List<UserWithRelationItem>>>()

    /**
     *  Resource indicating the follow request status between [user]
     *  and the currently authenticated user. Note that this is not
     *  relevant if we are looking at the current user profile.
     */
    val followRequestAsCurrentUser = MutableLiveData<Resource<FollowRequestItem>>()

    /**
     *  Used for graceful resource disposal.
     */
    private val compositeDisposable = CompositeDisposable()

    /**
     *  Sets all used resources to loading.
     */
    fun clearResources() {
        user.setLoading()
        userVlogs.setLoading()
        followingUsers.setLoading()
        followersAndFollowRequestingUsers.setLoading()
    }

    /**
     *  Deletes a vlog. This is only callable on vlogs
     *  owned by the currently authenticated user.
     *
     *  This updates the vlog list locally after deletion.
     *
     *  @param vlog The vlog object.
     */
    fun deleteVlog(vlog: Vlog) {
        if (vlog.userId != authUserUseCase.getSelfId()) {
            return
        }

        compositeDisposable.add(vlogUseCase
            .delete(vlog.id)
            .subscribe(
                {
                    if (userVlogs.value?.data != null) {
                        userVlogs.setSuccess(userVlogs.value!!.data!!.filter { it.vlog.id != vlog.id })
                    }
                },
                { userVlogs.setError(it.message) }
            )
        )
    }

    /**
     *  Gets a user and stores it in [user].
     *
     *  @param userId The user to get.
     *  @param refresh Force a data refresh.
     */
    fun getUser(userId: UUID, refresh: Boolean = false) = compositeDisposable.add(usersUseCase
        .getWithStats(userId, refresh)
        .subscribeOn(Schedulers.io())
        .map { it.mapToPresentation() }
        .subscribe(
            { user.setSuccess(it) },
            { user.setError(it.message) }
        )
    )

    /**
     *  Gets all vlogs that are owned by a given user.
     *
     *  @param userId The vlog owner.
     *  @param refresh Force a data refresh.
     */
    fun getVlogsByUser(userId: UUID, refresh: Boolean = false) = compositeDisposable.add(vlogUseCase
        .getAllForUser(userId, refresh)
        .doOnSubscribe { userVlogs.setLoading() }
        .subscribeOn(Schedulers.io())
        .map { list ->
            list.sortedByDescending { it.vlog.dateStarted }
                .map { wrapper -> wrapper.mapToPresentation() }
        }
        .subscribe(
            { userVlogs.setSuccess(it) },
            { userVlogs.setError(it.message) }
        )
    )

    /**
     *  Gets all users that a user is following.
     *
     *  @param userId The user that is following.
     *  @param refresh Force a data refresh.
     */
    fun getFollowing(userId: UUID, refresh: Boolean = false) = compositeDisposable.add(followUseCase
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
     *  Gets all followers for a given [userId]. Note that this converts each
     *  result item into a [UserWithRelationItem] with the requesting user id
     *  as [userId] and the follow request status as [FollowRequestStatus.ACCEPTED].
     *
     *  @param userId The user to get the followers for.
     *  @param refresh Force a data refresh.
     */
    fun getFollowers(userId: UUID, refresh: Boolean = false) = compositeDisposable.add(followUseCase
        .getFollowers(userId, refresh)
        .doOnSubscribe { followingUsers.setLoading() }
        .map { it.mapToPresentation() }
        .subscribe(
            {
                followersAndFollowRequestingUsers
                .setSuccess(it.mapToUserWithRelationItem(
                    requestingUserId = userId,
                    followRequestStatus = FollowRequestStatus.ACCEPTED
                ))
            },
            { followingUsers.setError(it.message) }
        )
    )

    // TODO This should be a single backend call.
    /**
     *  First does the same as [getFollowers] for the current user, then gets
     *  all pending incoming follow requests. Each request is then mapped to
     *  a [UserWithRelationItem] with status set to [FollowRequestStatus.PENDING].
     *
     *  The incoming requests are put first in the result set, all other user
     *  items are put after that in [followersAndFollowRequestingUsers].
     *
     *  @param refresh Force a data refresh.
     */
    fun getFollowersAndIncomingRequesters(refresh: Boolean) {
        val selfId = authUserUseCase.getSelfId()

        compositeDisposable.add(followUseCase
            .getFollowers(selfId, refresh)
            .doOnSubscribe { followingUsers.setLoading() }
            .map { it.mapToPresentation() }
            .subscribe(
                {
                    // TODO Fix.
//                    // If we get the followers, now get the incoming requests.
//                    followUseCase.getIncomingRequests()
//                        .mapSingle {
//
//                        }


                    followersAndFollowRequestingUsers
                        .setSuccess(it.mapToUserWithRelationItem(
                            requestingUserId = selfId,
                            followRequestStatus = FollowRequestStatus.ACCEPTED
                        ))
                },
                { followingUsers.setError(it.message) }
            )
        )
    }

    /**
     *  Updates the status of a follow request. The requesting
     *  user id is always the currently authenticated user.
     *
     *  @param receiverId The receiving user id.
     */
    fun getFollowRequest(receiverId: UUID) = compositeDisposable.add(followUseCase
        .get(authUserUseCase.getSelfId(), receiverId)
        .doOnSubscribe { followRequestAsCurrentUser.setLoading() }
        .subscribeOn(Schedulers.io())
        .subscribe(
            { followRequestAsCurrentUser.setSuccess(it.mapToPresentation()) },
            { followRequestAsCurrentUser.setError(it.message) }
        )
    )

    /**
     *  Send a follow request as the current user.
     *
     *  @param receiverId The user to be followed.
     */
    fun sendFollowRequest(receiverId: UUID) = compositeDisposable.add(followUseCase
        .sendFollowRequest(receiverId)
        .doOnSubscribe { followRequestAsCurrentUser.setLoading() }
        .subscribeOn(Schedulers.io())
        .subscribe(
            {
                // After sending the follow request, get it immediately.
                // Some updates might have taken place automatically.
                followUseCase
                    .get(authUserUseCase.getSelfId(), receiverId)
                    .map { followRequest -> followRequest.mapToPresentation() }
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                        { followRequestAsCurrentUser.setSuccess(it) },
                        { /* Does nothing */ }
                    )
            },
            { followRequestAsCurrentUser.setError(it.message) }
        )
    )

    /**
     *  Unfollow a user as the current user.
     *
     *  @param receiverId The user to unfollow.
     */
    fun unfollow(receiverId: UUID) = compositeDisposable.add(followUseCase
        .unfollow(receiverId)
        .doOnSubscribe { followRequestAsCurrentUser.setLoading() }
        .subscribeOn(Schedulers.io())
        .subscribe(
            { setFollowRequestAsNonExistent(receiverId) },
            { followRequestAsCurrentUser.setError(it.message) }
        )
    )

    /**
     *  Cancels a running follow request between the current user
     *  and [receiverId].
     *
     *  @param receiverId The follow request receiving user.
     */
    fun cancelFollowRequest(receiverId: UUID) = compositeDisposable.add(followUseCase
        .cancelFollowRequest(receiverId)
        .doOnSubscribe { followRequestAsCurrentUser.setLoading() }
        .subscribeOn(Schedulers.io())
        .subscribe(
            { setFollowRequestAsNonExistent(receiverId) },
            { followRequestAsCurrentUser.setError(it.message) }
        )
    )

    /**
     *  Graceful shutdown to dispose all resources.
     */
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
    private fun setFollowRequestAsNonExistent(receiverId: UUID) =
        followRequestAsCurrentUser.setSuccess(
            FollowRequestItem(
                requesterId = authUserUseCase.getSelfId(),
                receiverId = receiverId,
                requestStatus = FollowRequestStatus.NONEXISTENT,
                timeCreated = null
            )
        )
}
