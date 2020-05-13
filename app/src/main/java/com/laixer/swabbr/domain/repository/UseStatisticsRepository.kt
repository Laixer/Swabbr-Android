package com.laixer.swabbr.domain.repository

import com.laixer.swabbr.domain.model.UserStatistics
import io.reactivex.Single
import java.util.*

interface UserStatisticsRepository {

    fun getStatistics(userId: UUID, refresh: Boolean = false): Single<UserStatistics>

    fun getSelfStatistics(refresh: Boolean = false): Single<UserStatistics>
}
