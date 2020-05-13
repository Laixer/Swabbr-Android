package com.laixer.swabbr.data.datasource.cache

import com.laixer.cache.ReactiveCache
import com.laixer.swabbr.data.datasource.UserStatisticsCacheDataSource
import com.laixer.swabbr.domain.model.UserStatistics
import io.reactivex.Single
import java.util.*

class UserStatisticsCacheDataSourceImpl constructor(
    private val cache: ReactiveCache<UserStatistics>
) : UserStatisticsCacheDataSource {

    override fun getSelfStatistics(): Single<UserStatistics> = cache.load(self_key)

    override fun setSelfStatistics(statistics: UserStatistics): Single<UserStatistics> =
        cache.save(self_key, statistics)

    override fun getStatistics(userId: UUID): Single<UserStatistics> = cache.load(userId.toString())

    override fun setStatistics(userId: UUID, userStatistics: UserStatistics): Single<UserStatistics> =
        cache.save(userId.toString(), userStatistics)
}
