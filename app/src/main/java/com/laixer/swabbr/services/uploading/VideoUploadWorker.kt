package com.laixer.swabbr.services.uploading

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.laixer.swabbr.domain.model.UploadWrapper
import com.laixer.swabbr.services.users.UserManager
import com.laixer.swabbr.utils.files.ThumbnailHelper
import com.laixer.swabbr.utils.media.MediaConstants
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.io.File
import java.util.*

// TODO Always return failure instead of retry?
// TODO Cancellation (at logout)
/**
 *  Worker for managing content uploads.
 */
abstract class VideoUploadWorker(appContext: Context, workerParameters: WorkerParameters) :
    Worker(appContext, workerParameters), KoinComponent {

    private val userManager: UserManager by inject()

    /**
     *  First validates if we should execute, then if we can execute.
     *  Then this calls [getUploadWrapper]. The video content is then
     *  uploaded, followed by a call to [doAfterFilesUploaded].
     *
     *  Do not override this method, implement [getUploadWrapper] and
     *  [doAfterFilesUploaded] instead.
     */
    override fun doWork(): Result {
        // Retry check
        if (runAttemptCount >= MAX_RETRY_COUNT) {
            return Result.failure()
        }

        // First validate if we should execute this work in the first place.
        if (userManager.getUserIdOrNull() == null) {
            throw Exception("User isn't logged in - this should have been cancelled")
        }

        val userId = UUID.fromString(
            inputData.getString("userId")
                ?: throw Exception("Input data did not contain user id")
        )
        if (userId != userManager.getUserId()) {
            throw Exception("Specified user id doesn't match current user")
        }

        val fileAbsolutePath = inputData.getString("fileAbsolutePath")
            ?: throw Exception("Input data did not contain file uri")

        val videoFile = File(fileAbsolutePath)
        if (!videoFile.exists()) {
            throw Exception("Video file does not exist")
        }

        // TODO Dispatch on different thread? This is blocking now.
        return try {
            val uploadWrapper = getUploadWrapper()

            processAndUploadFiles(videoFile, uploadWrapper)

            doAfterFilesUploaded(uploadWrapper)

            Result.success()

        } catch (e: Exception) {
            Log.e(TAG, "Exception in uploading process", e)
            Result.retry()
        }
    }

    /**
     *  Override this to specify where we get our upload wrapper from.
     */
    protected abstract fun getUploadWrapper(): UploadWrapper

    /**
     *  Extracts thumbnail, uploads files and returns.
     *
     *  @return Successful or not.
     */
    private fun processAndUploadFiles(videoFile: File, uploadWrapper: UploadWrapper): Boolean {
        val thumbnailFile = ThumbnailHelper.createThumbnailFromVideoFile(applicationContext, videoFile)

        if (!uploadFile(videoFile, uploadWrapper.videoUploadUri, MediaConstants.VIDEO_MP4_MIME_TYPE)) {
            return false
        }

        if (!uploadFile(thumbnailFile, uploadWrapper.thumbnailUploadUri, MediaConstants.IMAGE_JPEG_MIME_TYPE)) {
            return false
        }

        return true
    }

    /**
     *  Override this method to determine what should happen
     *  after the file upload has completed.
     *
     *  @return Successful or not.
     */
    protected abstract fun doAfterFilesUploaded(uploadWrapper: UploadWrapper): Boolean

    /**
     *  Uploads a file to an external uri.
     *
     *  @param file The file to upload.
     *  @param uploadUri The uri to which the video file should be uploaded.
     *  @param contentTypeString The MIME type as string.
     *
     *  @return Successful or not.
     */
    private fun uploadFile(file: File, uploadUri: Uri, contentTypeString: String): Boolean {
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

    companion object {
        private val TAG = VideoUploadWorker::class.java.simpleName

        /**
         *  Add the current user id to the input data using this key.
         */
        const val KEY_USER_ID = "userId"

        /**
         *  Add the absolute file path to the input data using this key.
         */
        const val KEY_FILE_ABSOLUTE_PATH = "fileAbsolutePath"

        /**
         *  Maximum retries for an upload.
         */
        private const val MAX_RETRY_COUNT = 4
    }
}
