package com.laixer.swabbr.data.datasource

import com.laixer.swabbr.data.api.VlogLikeApi
import com.laixer.swabbr.data.model.mapToDomain
import com.laixer.swabbr.data.interfaces.VlogLikeDataSource
import com.laixer.swabbr.domain.model.VlogLike
import com.laixer.swabbr.domain.model.VlogLikeSummary
import com.laixer.swabbr.domain.model.LikingUserWrapper
import com.laixer.swabbr.domain.types.Pagination
import io.reactivex.Completable
import io.reactivex.Single
import java.util.*

/**
 *  Api based data source for vlog likes.
 */
class VlogLikeDataSourceImpl constructor(
    val api: VlogLikeApi
) : VlogLikeDataSource {
    override fun exists(vlogId: UUID, userId: UUID): Single<Boolean> = api.existsVlogLike(vlogId, userId)

    override fun get(vlogId: UUID, userId: UUID): Single<VlogLike> =
        api.getVlogLike(vlogId, userId).map { it.mapToDomain() }

    override fun getVlogLikeSummary(vlogId: UUID): Single<VlogLikeSummary> =
        api.getVlogLikeSummary(vlogId).map { it.mapToDomain() }

    override fun getLikes(vlogId: UUID, pagination: Pagination): Single<List<VlogLike>> =
        api.getVlogLikes(vlogId, pagination.sortingOrder.ordinal, pagination.limit, pagination.offset).map { it.mapToDomain() }

    override fun like(vlogId: UUID): Completable = api.like(vlogId)

    override fun unlike(vlogId: UUID): Completable = api.unlike(vlogId)
}
