package com.laixer.swabbr.data.datasource.model

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
) : Serializable {

    // Backend misnamed hostServer property, it is actually the full connect URL minus the streamKey (which can be anything)
    // Due to constraints we have to force RTMPS to RTMP on the client side because we don't support RTMPS right now
    fun getUrl(): String = "$hostServer/default"
        .replace("rtmps", "rtmp")
        .replace("2935", "1935")
}
