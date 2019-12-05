package com.laixer.sample.datasource.remote

import com.laixer.sample.data.datasource.FollowRemoteDataSource
import com.laixer.sample.datasource.model.mapToDomain
import io.reactivex.Single

class FollowRemoteDataSourceImpl constructor(
    private val api: FollowApi
) : FollowRemoteDataSource {

    override fun getFollowStatus(targetId: String): Single<String> =
        api.getFollowStatus(targetId)
            .map { it.mapToDomain() }
}
