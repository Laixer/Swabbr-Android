package com.laixer.swabbr.data.datasource.model

import android.net.Uri
import com.laixer.swabbr.domain.model.UploadWrapper
import com.squareup.moshi.Json
import java.util.*

/**
 * Contains upload uris for a vlog or reaction with id = [id].
 */
data class UploadWrapperEntity(
    @field:Json(name = "id") val id: UUID,
    @field:Json(name = "videoUploadUri") val videoUploadUri: Uri,
    @field:Json(name = "thumbnailUploadUri") val thumbnailUploadUri: Uri
)

/**
 * Map a upload wrapper object from data to domain.
 */
fun UploadWrapperEntity.mapToDomain(): UploadWrapper = UploadWrapper(
    id,
    videoUploadUri,
    thumbnailUploadUri
)
