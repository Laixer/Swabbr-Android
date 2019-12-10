package com.laixer.swabbr.data.datasource

import com.laixer.swabbr.domain.model.Reaction
import io.reactivex.Single

interface ReactionCacheDataSource {

    fun get(vlogId: String): Single<List<Reaction>>

    fun set(vlogId: String, list: List<Reaction>): Single<List<Reaction>>
}

interface ReactionRemoteDataSource {

    fun get(vlogId: String): Single<List<Reaction>>
}
