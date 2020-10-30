package com.laixer.swabbr.data.repository

import com.laixer.swabbr.data.datasource.ReactionCacheDataSource
import com.laixer.swabbr.data.datasource.ReactionRemoteDataSource
import com.laixer.swabbr.data.datasource.model.WatchReactionResponse
import com.laixer.swabbr.domain.model.Reaction
import com.laixer.swabbr.domain.model.UploadReaction
import com.laixer.swabbr.domain.repository.ReactionRepository
import io.reactivex.Completable
import io.reactivex.Single
import java.util.UUID

class ReactionRepositoryImpl constructor(
    private val cacheDataSource: ReactionCacheDataSource,
    private val remoteDataSource: ReactionRemoteDataSource
) : ReactionRepository {

    override fun get(vlogId: UUID, refresh: Boolean): Single<List<Reaction>> = remoteDataSource.get(vlogId)

    override fun new(targetVlogId: UUID): Single<UploadReaction> = remoteDataSource.new(targetVlogId)

    override fun finishUploading(reactionId: UUID): Completable = remoteDataSource.finishUploading(reactionId)

    override fun watch(reactionId: UUID): Single<WatchReactionResponse> = remoteDataSource.watch(reactionId)

}
