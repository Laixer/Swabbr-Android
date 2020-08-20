package com.laixer.swabbr.data.datasource.model

import com.squareup.moshi.Json

data class WatchVlogResponse(
    @field:Json(name = "endpointUrl") val endpointUrl: String,
    @field:Json(name = "token") val token: String,
    @field:Json(name = "vlogId") val vlogId: String
)

data class WatchLivestreamResponse(
    @field:Json(name = "endpointUrl") val endpointUrl: String,
    @field:Json(name = "token") val token: String,
    @field:Json(name = "liveUserId") val liveUserId: String,
    @field:Json(name = "liveLivestreamId") val liveLivestreamId: String,
    @field:Json(name = "liveVlogId") val liveVlogId: String
)



