package com.laixer.swabbr.domain.repository

import com.laixer.swabbr.data.datasource.model.WatchReactionResponse
import com.laixer.swabbr.domain.model.Reaction
import com.laixer.swabbr.domain.model.UploadReaction
import io.reactivex.Completable
import io.reactivex.Single
import java.util.UUID

interface ReactionRepository {

    fun get(vlogId: UUID, refresh: Boolean): Single<List<Reaction>>

    fun new(targetVlogId: UUID): Single<UploadReaction>

    fun finishUploading(reactionId: UUID): Completable

    fun watch(reactionId: UUID): Single<WatchReactionResponse>
}
