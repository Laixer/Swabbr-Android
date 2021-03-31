package com.laixer.swabbr.data.interfaces

import com.laixer.swabbr.domain.model.DatasetStats
import com.laixer.swabbr.domain.model.Reaction
import com.laixer.swabbr.domain.model.ReactionWrapper
import com.laixer.swabbr.domain.model.UploadWrapper
import com.laixer.swabbr.domain.types.Pagination
import io.reactivex.Completable
import io.reactivex.Single
import java.util.*

/**
 *  Caching for reactions.
 */
interface ReactionCacheDataSource {

    val key: String get() = "REACTIONS"

    fun get(vlogId: UUID): Single<List<Reaction>>

    fun set(vlogId: UUID, list: List<Reaction>): Single<List<Reaction>>
}

/**
 *  Data source for reactions.
 */
interface ReactionDataSource {

    fun delete(reactionId: UUID): Completable

    fun generateUploadWrapper(): Single<UploadWrapper>

    fun get(reactionId: UUID): Single<Reaction>

    fun getWrapper(reactionId: UUID): Single<ReactionWrapper>

    fun getForVlog(vlogId: UUID, pagination: Pagination = Pagination.latest()): Single<List<Reaction>>

    fun getWrappersForVlog(vlogId: UUID, pagination: Pagination = Pagination.latest()): Single<List<ReactionWrapper>>

    fun getCountForVlog(vlogId: UUID): Single<DatasetStats>

    fun post(reaction: Reaction): Completable

    fun update(reaction: Reaction): Completable
}
