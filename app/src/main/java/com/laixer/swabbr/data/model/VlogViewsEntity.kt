package com.laixer.swabbr.data.model

import com.laixer.swabbr.domain.model.VlogViews
import com.squareup.moshi.Json
import java.util.*

/**
 *  Data class for adding vlog views.
 */
data class VlogViewsEntity(@field:Json(name = "vlogViewPairs") val vlogViewPairs: Map<UUID, Int>)

fun VlogViews.mapToData(): VlogViewsEntity = VlogViewsEntity(
    vlogViewPairs = mapOf(vlogId to 1)
)
