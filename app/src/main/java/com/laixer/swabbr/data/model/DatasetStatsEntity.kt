package com.laixer.swabbr.data.model

import com.laixer.swabbr.domain.model.DatasetStats
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
