package com.laixer.swabbr.utils.files

import android.content.Context
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.os.CancellationSignal
import android.util.Size
import com.laixer.swabbr.utils.media.MediaConstants
import java.io.File
import java.io.FileOutputStream

/**
 *  Contains functionality to generate thumbnails for us.
 */
class ThumbnailHelper {
    companion object {
        /**
         *  Creates a thumbnail from a video file.
         */
        fun createThumbnailFromVideoFile(context: Context, videoFile: File, size: Size = DEFAULT_SIZE): File {
            // TODO Use cancellation for timeout?
            val cancellationSignal = CancellationSignal()

            val thumbnail = ThumbnailUtils.createVideoThumbnail(videoFile, size, cancellationSignal)

            val thumbnailFile = FileHelper.createFile(context, FILE_NAME_BASE, DEFAULT_MIME_TYPE, true)
            val os = FileOutputStream(thumbnailFile)
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, os)
            os.flush()
            os.close()

            return thumbnailFile
        }

        private const val FILE_NAME_BASE = "thumbnail"
        private const val DEFAULT_MIME_TYPE = MediaConstants.IMAGE_JPEG_MIME_TYPE
        private val DEFAULT_SIZE = MediaConstants.SIZE_720p
    }
}
