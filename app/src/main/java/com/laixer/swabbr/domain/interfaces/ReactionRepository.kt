package com.laixer.swabbr.domain.interfaces

import com.laixer.swabbr.domain.model.DatasetStats
import com.laixer.swabbr.domain.model.Reaction
import com.laixer.swabbr.domain.model.UploadWrapper
import com.laixer.swabbr.domain.types.Pagination
import io.reactivex.Completable
import io.reactivex.Single
import java.util.*

/**
 *  Interface for a reaction repository.
 */
interface ReactionRepository {
    fun delete(reactionId: UUID): Completable

    fun generateUploadWrapper(): Single<UploadWrapper>

    fun get(reactionId: UUID): Single<Reaction>

    fun getForVlog(vlogId: UUID, pagination: Pagination = Pagination.latest()): Single<List<Reaction>>

    fun getCountForVlog(vlogId: UUID): Single<DatasetStats>

    fun post(reaction: Reaction): Completable

    fun update(reaction: Reaction): Completable
}
