package com.laixer.swabbr.data.datasource.remote

import com.laixer.swabbr.data.datasource.ReactionRemoteDataSource
import com.laixer.swabbr.datasource.model.mapToDomain
import com.laixer.swabbr.datasource.model.remote.ReactionsApi
import com.laixer.swabbr.domain.model.Reaction
import io.reactivex.Single
import java.util.UUID

class ReactionRemoteDataSourceImpl constructor(
    val api: ReactionsApi
) : ReactionRemoteDataSource {

    override fun get(vlogId: UUID): Single<List<Reaction>> = api.getReactions(vlogId).map { it.mapToDomain() }
}
