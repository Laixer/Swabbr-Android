package com.laixer.swabbr.services.uploading

import android.content.Context
import androidx.work.*
import com.laixer.swabbr.domain.model.UploadWrapper
import com.laixer.swabbr.domain.usecase.ReactionUseCase
import com.laixer.swabbr.presentation.model.ReactionItem
import com.laixer.swabbr.presentation.model.mapToDomain
import org.koin.core.inject
import java.io.File
import java.time.Duration
import java.util.*

/**
 *  Worker for managing reaction uploads.
 */
class ReactionUploadWorker(appContext: Context, workerParameters: WorkerParameters) :
    VideoUploadWorker(appContext, workerParameters) {

    private val reactionUseCase: ReactionUseCase by inject()

    /**
     *  Get a reaction upload wrapper.
     */
    override fun getUploadWrapper(): UploadWrapper = reactionUseCase.generateUploadWrapper().blockingGet()

    // TODO This will never detect failure.
    /**
     *  Post the reaction to the backend.
     *
     *  @return Successful or not.
     */
    override fun doAfterFilesUploaded(uploadWrapper: UploadWrapper): Boolean {
        // TODO Do this check before upload.
        val targetVlogId = UUID.fromString(
            inputData.getString("targetVlogId")
                ?: throw Exception("Input data did not contain target vlog id")
        )
        val isPrivate = inputData.getBoolean("isPrivate", false)

        reactionUseCase.postReaction(
            ReactionItem.createForPosting(
                id = uploadWrapper.id,
                targetVlogId = targetVlogId,
                isPrivate = isPrivate
            ).mapToDomain()
        ).blockingAwait()

        return true
    }

    companion object {
        private val TAG = ReactionUploadWorker::class.java.simpleName

        /**
         *  Creates a new [WorkRequest] instance for this
         *  worker to execute and enqueues it right away.
         */
        fun enqueue(context: Context, userId: UUID, videoFile: File, targetVlogId: UUID, isPrivate: Boolean) {
            val uploadRequest: WorkRequest = OneTimeWorkRequestBuilder<ReactionUploadWorker>()
                .addTag(WORK_TAG)
                .setInputData(
                    workDataOf(
                        "userId" to userId.toString(),
                        "fileAbsolutePath" to videoFile.absolutePath,
                        "targetVlogId" to targetVlogId.toString(),
                        "isPrivate" to isPrivate
                    )
                )
                .build()
            WorkManager.getInstance(context).enqueue(uploadRequest)
        }

        /**
         *  Public tag for this kind of work.
         */
        const val WORK_TAG = "UPLOAD_REACTION"

        /**
         *  Add the public/private flag to the input data using this key.
         */
        const val KEY_IS_PRIVATE = "isPrivate"

        /**
         *  Add the target vlog id to the input data using this key.
         */
        const val KEY_TARGET_VLOG_ID = "targetVlogId"
    }
}
