package com.laixer.swabbr.domain.usecase

import com.laixer.swabbr.domain.model.LikingUserWrapper
import com.laixer.swabbr.domain.interfaces.VlogLikeRepository
import com.laixer.swabbr.domain.types.Pagination
import io.reactivex.Single
import java.util.*

// TODO Use refresh options.
/**
 *  Use case for displaying the vlog like overview tab. Note
 *  that this does not handle vlog likes with regards to the
 *  vlogs itself, only the overview with their users.
 */
class VlogLikeOverviewUseCase constructor(
    private val vlogLikeRepository: VlogLikeRepository
) {
    /**
     *  Gets an overview of all vlog liking users for
     *  the currently authenticated user.
     */
    fun getVlogLikingUsers(refresh: Boolean): Single<List<LikingUserWrapper>> = vlogLikeRepository
        .getVlogLikingUsers(Pagination.latest())
}
