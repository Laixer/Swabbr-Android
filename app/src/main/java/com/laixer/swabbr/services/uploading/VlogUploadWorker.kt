package com.laixer.swabbr.services.uploading

import android.content.Context
import androidx.work.*
import com.laixer.swabbr.domain.model.UploadWrapper
import com.laixer.swabbr.domain.usecase.ReactionUseCase
import com.laixer.swabbr.domain.usecase.VlogUseCase
import com.laixer.swabbr.presentation.model.ReactionItem
import com.laixer.swabbr.presentation.model.VlogItem
import com.laixer.swabbr.presentation.model.mapToDomain
import org.koin.core.inject
import java.io.File
import java.time.Duration
import java.util.*

/**
 *  Worker for managing vlog uploads.
 */
class VlogUploadWorker(appContext: Context, workerParameters: WorkerParameters) :
    VideoUploadWorker(appContext, workerParameters) {

    private val vlogUseCase: VlogUseCase by inject()

    /**
     *  Get a vlog upload wrapper.
     */
    override fun getUploadWrapper(): UploadWrapper = vlogUseCase.generateUploadWrapper().blockingGet()

    // TODO This will never detect failure.
    /**
     *  Post the vlog to the backend.
     *
     *  @return Successful or not.
     */
    override fun doAfterFilesUploaded(uploadWrapper: UploadWrapper): Boolean {
        // TODO Do this check before upload.
        val isPrivate = inputData.getBoolean("isPrivate", false)

        vlogUseCase.postVlog(
            VlogItem.createForPosting(
                id = uploadWrapper.id,
                isPrivate = isPrivate
            ).mapToDomain()
        ).blockingAwait()

        return true
    }

    companion object {
        private val TAG = VlogUploadWorker::class.java.simpleName

        /**
         *  Creates a new [WorkRequest] instance for this
         *  worker to execute and enqueues it right away.
         */
        fun enqueue(context: Context, userId: UUID, videoFile: File, isPrivate: Boolean) {
            val uploadRequest: WorkRequest = OneTimeWorkRequestBuilder<VlogUploadWorker>()
                .addTag(WORK_TAG)
                .setInputData(
                    workDataOf(
                        "userId" to userId.toString(),
                        "fileAbsolutePath" to videoFile.absolutePath,
                        "isPrivate" to isPrivate
                    )
                )
                .build()
            WorkManager.getInstance(context).enqueue(uploadRequest)
        }

        /**
         *  Public tag for this kind of work.
         */
        const val WORK_TAG = "UPLOAD_VLOG"

        /**
         *  Add the public/private flag to the input data using this key.
         */
        const val KEY_IS_PRIVATE = "isPrivate"
    }
}
