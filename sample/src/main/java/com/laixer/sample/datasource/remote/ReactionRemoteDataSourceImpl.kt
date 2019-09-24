package com.laixer.sample.datasource.remote

import com.laixer.sample.data.datasource.ReactionRemoteDataSource
import com.laixer.sample.datasource.model.mapToDomain
import com.laixer.sample.domain.model.Reaction
import io.reactivex.Single

class ReactionRemoteDataSourceImpl constructor(
    private val api: ReactionsApi
) : ReactionRemoteDataSource {

    override fun get(vlogId: String): Single<List<Reaction>> =
        api.getReactions(vlogId)
            .map { it.mapToDomain() }
}
