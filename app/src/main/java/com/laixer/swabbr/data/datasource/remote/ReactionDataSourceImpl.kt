package com.laixer.swabbr.data.datasource.remote

import com.laixer.swabbr.data.datasource.ReactionDataSource
import com.laixer.swabbr.data.datasource.model.mapToData
import com.laixer.swabbr.data.datasource.model.mapToDomain
import com.laixer.swabbr.data.datasource.model.remote.ReactionApi
import com.laixer.swabbr.domain.model.DatasetStats
import com.laixer.swabbr.domain.model.Reaction
import com.laixer.swabbr.domain.model.UploadWrapper
import io.reactivex.Completable
import io.reactivex.Single
import java.util.*

class ReactionDataSourceImpl constructor(
    val api: ReactionApi
) : ReactionDataSource {

    override fun delete(reactionId: UUID): Completable = api.delete(reactionId)

    override fun generateUploadWrapper(): Single<UploadWrapper> = api.generateUploadWrapper().map { it.mapToDomain() }

    override fun get(reactionId: UUID): Single<Reaction> = api.getReaction(reactionId).map { it.mapToDomain() }

    override fun getForVlog(vlogId: UUID): Single<List<Reaction>> =
        api.getReactionsForVlog(vlogId).map { it.mapToDomain() }

    override fun getCountForVlog(vlogId: UUID): Single<DatasetStats> =
        api.getReactionCountForVlog(vlogId).map { it.mapToDomain() }

    override fun post(reaction: Reaction): Completable = api.postReaction(reaction.mapToData())

    override fun update(reaction: Reaction): Completable = api.updateReaction(reaction.mapToData())
}
