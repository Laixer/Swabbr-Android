package com.laixer.swabbr.datasource.remote

import com.laixer.swabbr.data.datasource.ReactionRemoteDataSource
import com.laixer.swabbr.datasource.model.mapToDomain
import com.laixer.swabbr.domain.model.Reaction
import io.reactivex.Single

class ReactionRemoteDataSourceImpl constructor(
    private val api: ReactionsApi
) : ReactionRemoteDataSource {

    override fun get(vlogId: String): Single<List<Reaction>> =
        api.getReactions(vlogId)
            .map { it.mapToDomain() }
}
