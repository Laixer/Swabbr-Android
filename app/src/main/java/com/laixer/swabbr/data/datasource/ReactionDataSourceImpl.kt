package com.laixer.swabbr.data.datasource

import com.laixer.swabbr.data.api.ReactionApi
import com.laixer.swabbr.data.interfaces.ReactionDataSource
import com.laixer.swabbr.data.model.mapToData
import com.laixer.swabbr.data.model.mapToDomain
import com.laixer.swabbr.domain.model.DatasetStats
import com.laixer.swabbr.domain.model.Reaction
import com.laixer.swabbr.domain.model.ReactionWrapper
import com.laixer.swabbr.domain.model.UploadWrapper
import com.laixer.swabbr.domain.types.Pagination
import io.reactivex.Completable
import io.reactivex.Single
import java.util.*

class ReactionDataSourceImpl constructor(
    val api: ReactionApi
) : ReactionDataSource {

    override fun delete(reactionId: UUID): Completable = api.delete(reactionId)

    override fun generateUploadWrapper(): Single<UploadWrapper> = api.generateUploadWrapper().map { it.mapToDomain() }

    override fun get(reactionId: UUID): Single<Reaction> = api.getReaction(reactionId).map { it.mapToDomain() }

    override fun getWrapper(reactionId: UUID): Single<ReactionWrapper> =
        api.getReactionWrapper(reactionId).map { it.mapToDomain() }

    override fun getForVlog(vlogId: UUID, pagination: Pagination): Single<List<Reaction>> =
        api.getReactionsForVlog(vlogId, pagination.sortingOrder.ordinal, pagination.limit, pagination.offset)
            .map { it.mapToDomain() }

    override fun getWrappersForVlog(vlogId: UUID, pagination: Pagination): Single<List<ReactionWrapper>> =
        api.getReactionWrappersForVlog(vlogId, pagination.sortingOrder.ordinal, pagination.limit, pagination.offset)
            .map { it.mapToDomain() }

    override fun getCountForVlog(vlogId: UUID): Single<DatasetStats> =
        api.getReactionCountForVlog(vlogId).map { it.mapToDomain() }

    override fun post(reaction: Reaction): Completable = api.postReaction(reaction.mapToData())

    override fun update(reaction: Reaction): Completable = api.updateReaction(reaction.mapToData())
}
