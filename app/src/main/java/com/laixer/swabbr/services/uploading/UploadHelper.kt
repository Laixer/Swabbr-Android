package com.laixer.swabbr.services.uploading

import android.net.Uri
import android.util.Log
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.File

/**
 *  Contains generic uploading functionality.
 */
class UploadHelper {
    companion object {
        val TAG = UploadHelper::class.java.simpleName

        /**
         *  Uploads a file to an external uri.
         *
         *  @param file The file to upload.
         *  @param uploadUri The uri to which the video file should be uploaded.
         *  @param contentTypeString The MIME type as string.
         *
         *  @return Successful or not.
         */
        fun uploadFile(file: File, uploadUri: Uri, contentTypeString: String): Boolean {
            try {
                val contentType = MediaType.parse(contentTypeString)
                if (contentType == null) {
                    Log.e(TAG, "Could not parse content type $contentTypeString")
                    return false
                }

                // We only specify the actual content type here. All other
                // content types reference to the multipart request type.
                val body = RequestBody.create(contentType, file)

                val request = Request.Builder()
                    .url(uploadUri.toString())
                    .put(body)
                    .addHeader("Content-Type", contentTypeString)
                    .build()

                val response = OkHttpClient.Builder()
                    .build()
                    .newCall(request)
                    .execute()

                if (!response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    Log.e(TAG, "Upload failed. Response body: $responseBody")
                    return false
                } else {
                    Log.d(TAG, "Upload succeeded for file ${file.absolutePath}")
                }

                return true

            } catch (e: Exception) {
                Log.e(TAG, "Exception in upload of file ${file.absolutePath}", e)
                return false
            }
        }
    }
}
