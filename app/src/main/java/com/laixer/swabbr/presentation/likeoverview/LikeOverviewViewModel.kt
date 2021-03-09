package com.laixer.swabbr.presentation.likeoverview

import com.laixer.presentation.setError
import com.laixer.presentation.setLoading
import com.laixer.presentation.setSuccess
import com.laixer.swabbr.domain.usecase.FollowUseCase
import com.laixer.swabbr.domain.usecase.VlogLikeOverviewUseCase
import com.laixer.swabbr.presentation.abstraction.UserWithRelationListViewModelBase
import com.laixer.swabbr.presentation.model.mapToPresentation
import io.reactivex.schedulers.Schedulers
import java.util.*

/**
 *  View model for the vlog liking users overview.
 */
class LikeOverviewViewModel constructor(
    private val vlogLikeOverviewUseCase: VlogLikeOverviewUseCase,
    followUseCase: FollowUseCase
) : UserWithRelationListViewModelBase(followUseCase) {
    /**
     *  Gets the vlog liking users for the currently authenticated
     *  user and stores them in [likingUserWrappers].
     *
     *  @param refresh Force a data refresh.
     */
    fun getLikingUserWrappers(refresh: Boolean = true) = compositeDisposable.add(
        vlogLikeOverviewUseCase.getVlogLikingUsers(refresh = refresh)
            .doOnSubscribe { users.setLoading() }
            .subscribeOn(Schedulers.io())
            .subscribe(
                { users.setSuccess(it.mapToPresentation()) },
                { users.setError(it.message) }
            )
    )
}
