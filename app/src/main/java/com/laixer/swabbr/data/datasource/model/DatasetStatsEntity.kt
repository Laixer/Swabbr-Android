package com.laixer.swabbr.data.datasource.model

import com.laixer.swabbr.domain.model.DatasetStats
import com.laixer.swabbr.domain.model.FollowRequest
import com.squareup.moshi.Json

/**
 * Contains statistics about a given dataset.
 */
data class DatasetStatsEntity(
    @field:Json(name = "count") val count: Int
)

/**
 * Map a dataset stats object from data to domain.
 */
fun DatasetStatsEntity.mapToDomain(): DatasetStats = DatasetStats(
    count
)
