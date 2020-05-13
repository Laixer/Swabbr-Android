package com.laixer.swabbr.data.datasource

import com.laixer.swabbr.domain.model.UserStatistics
import io.reactivex.Single
import java.util.UUID

interface UserStatisticsCacheDataSource {

    val self_key: String
        get() = "self"

    fun getSelfStatistics(): Single<UserStatistics>

    fun setSelfStatistics(statistics: UserStatistics): Single<UserStatistics>

    fun getStatistics(userId: UUID): Single<UserStatistics>

    fun setStatistics(userId: UUID, userStatistics: UserStatistics): Single<UserStatistics>

}

interface UserStatisticsRemoteDataSource {

    fun getSelfStatistics(): Single<UserStatistics>

    fun getStatistics(userId: UUID): Single<UserStatistics>
}
