package com.laixer.swabbr.utils

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 *  Contains helper functionality for managing files.
 */
class FileHelper {
    companion object {
        /**
         *  Creates a new file with filename based on the current date.
         */
        fun createFile(context: Context, extension: String): File {
            val sdf = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS", Locale.ENGLISH)
            return File(context.filesDir, "VID_${sdf.format(Date())}.$extension")
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
