package com.laixer.swabbr.presentation.abstraction

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.laixer.swabbr.domain.types.FollowRequestStatus
import com.laixer.swabbr.domain.usecase.FollowUseCase
import com.laixer.swabbr.extensions.cascadeFollowAction
import com.laixer.swabbr.presentation.model.UserWithRelationItem
import com.laixer.swabbr.utils.resources.Resource
import io.reactivex.schedulers.Schedulers
import java.util.*

/**
 *  View model for displaying an overview of users with a
 *  follow button, including local and remote operations
 *  for these follow buttons (unfollow, cancel, follow).
 */
open class UserWithRelationListViewModelBase constructor(
    private val followUseCase: FollowUseCase
) : ViewModelBase() {
    /**
     *  Public resource in which we store the user with relation list.
     *  This is public so it can be observed.
     */
    val users = MutableLiveData<Resource<List<UserWithRelationItem>>>()

    /**
     *  Unfollow a given user as the current user. The operation
     *  is then cascaded into [users].
     *
     *  @param userId The user to follow.
     */
    fun unfollow(userId: UUID) {
        // First update locally, then perform the action.
        users.cascadeFollowAction(userId, FollowRequestStatus.NONEXISTENT)

        compositeDisposable.add(
            followUseCase.unfollow(userId)
                .subscribeOn(Schedulers.io())
                .subscribe({}, { Log.e(TAG, "Could not unfollow - ${it.message}") })
        )
    }

    /**
     *  Send a follow request from the current user to a given user
     *  id. The follow request is then cascaded into [users].
     *
     *  @param userId The user to follow.
     */
    fun follow(userId: UUID) {
        // First update locally, then perform the action.
        users.cascadeFollowAction(userId, FollowRequestStatus.PENDING)

        compositeDisposable.add(
            followUseCase.sendFollowRequest(userId)
                .subscribeOn(Schedulers.io())
                .subscribe({}, { Log.e(TAG, "Could not follow - ${it.message}") })
        )
    }

    /**
     *  Cancels a follow request from the current user to a given user
     *  id. The follow request is then cascaded into [users].
     *
     *  @param userId The user to follow.
     */
    fun cancelFollowRequest(userId: UUID) {
        // First update locally, then perform the action.
        users.cascadeFollowAction(userId, FollowRequestStatus.NONEXISTENT)

        compositeDisposable.add(
            followUseCase.cancelFollowRequest(userId)
                .subscribeOn(Schedulers.io())
                .subscribe({}, { Log.e(TAG, "Could not cancel follow request - ${it.message}") })
        )
    }

    companion object {
        private val TAG = UserWithRelationListViewModelBase::class.java.simpleName
    }
}
