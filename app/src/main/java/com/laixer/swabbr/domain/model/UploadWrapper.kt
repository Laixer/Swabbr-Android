package com.laixer.swabbr.domain.model

import android.net.Uri
import java.util.*

/**
 * Contains upload uris for a vlog or reaction with id = [id].
 */
data class UploadWrapper(
    val id: UUID,
    val videoUploadUri: Uri,
    val thumbnailUploadUri: Uri
)
