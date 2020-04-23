package com.laixer.swabbr.data.datasource

import com.laixer.swabbr.domain.model.Reaction
import io.reactivex.Single
import java.util.UUID

interface ReactionCacheDataSource {

    val key: String
        get() = "REACTIONS"

    fun get(vlogId: UUID): Single<List<Reaction>>

    fun set(vlogId: UUID, list: List<Reaction>): Single<List<Reaction>>
}

interface ReactionRemoteDataSource {

    fun get(vlogId: UUID): Single<List<Reaction>>
}
