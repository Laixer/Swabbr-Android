package com.laixer.swabbr.datasource.model

import com.squareup.moshi.Json

data class WatchResponse(
    @field:Json(name = "liveUserId") val liveUserId: String,
    @field:Json(name = "liveLivestreamId") val liveLivestreamId: String,
    @field:Json(name = "liveVlogId") val liveVlogId: String,
    @field:Json(name = "endpointUrl") val endpointUrl: Int,
    @field:Json(name = "token") val token: String
)
