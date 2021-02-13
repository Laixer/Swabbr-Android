package com.laixer.swabbr.presentation.likeoverview

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.laixer.presentation.Resource
import com.laixer.presentation.setError
import com.laixer.presentation.setLoading
import com.laixer.presentation.setSuccess
import com.laixer.swabbr.domain.types.FollowRequestStatus
import com.laixer.swabbr.domain.usecase.FollowUseCase
import com.laixer.swabbr.domain.usecase.VlogLikeOverviewUseCase
import com.laixer.swabbr.extensions.cascadeFollowAction
import com.laixer.swabbr.presentation.model.UserWithRelationItem
import com.laixer.swabbr.presentation.model.mapToPresentation
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.*

/**
 *  View model for the vlog liking users overview.
 */
class LikeOverviewViewModel constructor(
    private val vlogLikeOverviewUseCase: VlogLikeOverviewUseCase,
    private val followUseCase: FollowUseCase
) : ViewModel() {
    /**
     *  Resource in which we store the vlog liking user wrapper list.
     */
    val likingUserWrappers = MutableLiveData<Resource<List<UserWithRelationItem>>>()

    private val compositeDisposable = CompositeDisposable()

    /**
     *  Gets the vlog liking users for the currently authenticated
     *  user and stores them in [likingUserWrappers].
     *
     *  @param refresh Force a data refresh.
     */
    fun getLikingUserWrappers(refresh: Boolean = true) = compositeDisposable.add(
        vlogLikeOverviewUseCase.getVlogLikingUsers(refresh = refresh)
            .doOnSubscribe { likingUserWrappers.setLoading() }
            .subscribeOn(Schedulers.io())
            .subscribe(
                { likingUserWrappers.setSuccess(it.mapToPresentation()) },
                { likingUserWrappers.setError(it.message) }
            )
    )

    /**
     *  Sends a follow request from the current user to a given user
     *  id. The follow request is then cascaded into [likingUserWrappers].
     *
     *  @param userId The user to follow.
     */
    fun follow(userId: UUID) {
        // First update locally, then perform the action.
        likingUserWrappers.cascadeFollowAction(userId, FollowRequestStatus.PENDING)

        compositeDisposable.add(
            followUseCase.sendFollowRequest(userId)
                .subscribeOn(Schedulers.io())
                .subscribe()
        )
    }


    /**
     *  Called on graceful disposal. This will dispose the [compositeDisposable]
     */
    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}
