package com.laixer.swabbr.data.datasource.remote

import com.laixer.swabbr.data.datasource.ReactionRemoteDataSource
import com.laixer.swabbr.data.datasource.model.NewReaction
import com.laixer.swabbr.data.datasource.model.WatchReactionResponse
import com.laixer.swabbr.data.datasource.model.mapToDomain
import com.laixer.swabbr.data.datasource.model.remote.ReactionsApi
import com.laixer.swabbr.domain.model.Reaction
import com.laixer.swabbr.domain.model.UploadReaction
import io.reactivex.Completable
import io.reactivex.Single
import java.util.UUID

class ReactionRemoteDataSourceImpl constructor(
    val api: ReactionsApi
) : ReactionRemoteDataSource {

    override fun get(vlogId: UUID): Single<List<Reaction>> = api.getReactions(vlogId).map { it.reactions.mapToDomain() }

    override fun new(targetVlogId: UUID): Single<UploadReaction> = api.newReaction(NewReaction(targetVlogId.toString(), false)).map { it.mapToDomain() }

    override fun finishUploading(reactionId: UUID): Completable = api.finishUploading(reactionId)

    override fun watch(reactionId: UUID): Single<WatchReactionResponse> = api.watch(reactionId)

}
