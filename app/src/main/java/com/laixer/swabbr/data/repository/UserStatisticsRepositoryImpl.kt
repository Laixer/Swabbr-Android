package com.laixer.swabbr.data.repository

import com.laixer.swabbr.data.datasource.UserStatisticsCacheDataSource
import com.laixer.swabbr.data.datasource.UserStatisticsRemoteDataSource
import com.laixer.swabbr.domain.model.UserStatistics
import com.laixer.swabbr.domain.repository.UserStatisticsRepository
import io.reactivex.Single
import java.util.*

class UserStatisticsRepositoryImpl constructor(
    private val cacheDataSource: UserStatisticsCacheDataSource,
    private val remoteDataSource: UserStatisticsRemoteDataSource
) : UserStatisticsRepository {

    override fun getStatistics(userId: UUID, refresh: Boolean): Single<UserStatistics> = when (refresh) {
        true -> remoteDataSource.getStatistics(userId).flatMap { cacheDataSource.setStatistics(userId, it) }
        false -> cacheDataSource.getStatistics(userId).onErrorResumeNext { getStatistics(userId, true) }
    }

    override fun getSelfStatistics(refresh: Boolean): Single<UserStatistics> = when (refresh) {
        true -> remoteDataSource.getSelfStatistics().flatMap { cacheDataSource.setSelfStatistics(it) }
        false -> cacheDataSource.getSelfStatistics().onErrorResumeNext { getSelfStatistics(true) }
    }

}
