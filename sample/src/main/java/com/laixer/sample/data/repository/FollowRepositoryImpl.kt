package com.laixer.sample.data.repository

import com.laixer.sample.data.datasource.FollowCacheDataSource
import com.laixer.sample.data.datasource.FollowRemoteDataSource
import com.laixer.sample.domain.model.FollowRequest
import com.laixer.sample.domain.repository.FollowRepository
import io.reactivex.Single

class FollowRepositoryImpl constructor(
    private val cacheDataSource: FollowCacheDataSource,
    private val remoteDataSource: FollowRemoteDataSource
) : FollowRepository {

    override fun get(userId: String): Single<List<FollowRequest>> =
        remoteDataSource.get(userId)
            .flatMap { cacheDataSource.set(it)}

    override fun get(receiverId: String): Single<FollowRequest> =
        remoteDataSource.get(receiverId)
            .flatMap { cacheDataSource.set(it)}

    override fun requestFollow(receiverId: String): Single<FollowRequest> =
        remoteDataSource.get(receiverId)
            .flatMap { cacheDataSource.set(it) }

    override fun requestUnfollow(receiverId: String): Single<FollowRequest> =
        remoteDataSource.get(receiverId)
            .flatMap { cacheDataSource.set(it) }
}
