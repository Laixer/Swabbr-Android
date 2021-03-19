package com.laixer.swabbr.utils.files

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.webkit.MimeTypeMap
import java.io.File
import java.io.FileOutputStream
import java.lang.Error
import java.text.SimpleDateFormat
import java.util.*

/**
 *  Contains helper functionality for managing files.
 */
class FileHelper {
    companion object {
        // TODO Use mime type
        /**
         *  Creates a new file with filename based on the current date.
         *
         *  @param context The context in which this is created.
         *  @param name File name without extension.
         *  @param mimeType The file mime type, used for extension.
         *  @param useTimeStamp Whether or not to include a timestamp in the file name.
         */
        fun createFile(context: Context, name: String, mimeType: String, useTimeStamp: Boolean = true): File {
            val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
                ?: throw Exception("Could not find extension for mime type $mimeType")

            val fileName = if (useTimeStamp) {
                "$name.$extension"
            } else {
                val sdf = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS", Locale.ENGLISH).format(Date())
                "${name}_$sdf.$extension"
            }

            return File(context.filesDir, fileName)
        }

        /**
         *  Write a bitmap to an existing file.
         *
         *  @return True if successful.
         */
        fun writeBitmapToFile(
            bitmap: Bitmap,
            file: File,
            compressFormat: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
            quality: Int = 100
        ): Boolean {
            return try {
                val os = FileOutputStream(file)
                bitmap.compress(compressFormat, quality, os)
                os.flush()
                os.close()

                true
            } catch (e: Exception) {
                Log.e(TAG, "Couldn't write bitmap to file $file")

                false
            }
        }

        // Log tag.
        private val TAG = FileHelper::class.java.simpleName
    }
}
