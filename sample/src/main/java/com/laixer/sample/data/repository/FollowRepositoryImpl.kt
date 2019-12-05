package com.laixer.sample.data.repository

import com.laixer.sample.data.datasource.FollowRemoteDataSource
import com.laixer.sample.domain.repository.FollowRepository
import io.reactivex.Single

class FollowRepositoryImpl constructor(
    private val remoteDataSource: FollowRemoteDataSource
) : FollowRepository {
    override fun getFollowStatus(targetId: String): Single<String> =
        remoteDataSource.getFollowStatus(targetId)
}
