package com.laixer.swabbr.data.datasource.remote

import com.laixer.swabbr.data.datasource.UserStatisticsRemoteDataSource
import com.laixer.swabbr.datasource.model.mapToDomain
import com.laixer.swabbr.datasource.model.remote.UsersApi
import com.laixer.swabbr.domain.model.UserStatistics
import io.reactivex.Single
import java.util.*

class UserStatisticsRemoteDataSourceImpl constructor(
    val api: UsersApi
) : UserStatisticsRemoteDataSource {

    override fun getSelfStatistics(): Single<UserStatistics> = api.getSelfStatistics().map { it.mapToDomain() }
    override fun getStatistics(userId: UUID): Single<UserStatistics> = api.getStatistics(userId).map { it.mapToDomain() }
}
