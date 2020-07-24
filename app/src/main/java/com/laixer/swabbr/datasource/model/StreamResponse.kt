package com.laixer.swabbr.datasource.model

import com.squareup.moshi.Json
import java.io.Serializable

data class StreamResponse(
    @field:Json(name = "vlogId") val vlogId: String,
    @field:Json(name = "livestreamId") val livestreamId: String,
    @field:Json(name = "hostServer") val hostServer: String,
    @field:Json(name = "hostPort") val hostPort: Int,
    @field:Json(name = "applicationName") val applicationName: String,
    @field:Json(name = "streamKey") val streamKey: String,
    @field:Json(name = "username") val username: String,
    @field:Json(name = "password") val password: String
) : Serializable

fun StreamResponse.getUrl(): String = "rtmp://$hostServer:$hostPort/$applicationName/$streamKey/default"
