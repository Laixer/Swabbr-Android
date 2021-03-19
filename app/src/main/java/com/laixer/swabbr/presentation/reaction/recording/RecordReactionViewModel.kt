package com.laixer.swabbr.presentation.reaction.recording

import android.content.Context
import android.util.Log
import androidx.core.net.toUri
import com.laixer.swabbr.domain.usecase.ReactionUseCase
import com.laixer.swabbr.presentation.model.ReactionItem
import com.laixer.swabbr.presentation.model.mapToDomain
import com.laixer.swabbr.presentation.recording.UploadVideoViewModel
import com.laixer.swabbr.utils.files.ThumbnailHelper
import com.laixer.swabbr.utils.media.MediaConstants
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import java.io.File
import java.util.*

// TODO Pretty much a duplicate of VlogRecordingViewModel. All todos are located there.
/**
 *  View model containing functionality for posting reactions.
 */
class RecordReactionViewModel constructor(
    mHttpClient: OkHttpClient,
    private val reactionsUseCase: ReactionUseCase
) : UploadVideoViewModel(mHttpClient) {
    /**
     *  Uploads a [ReactionItem] including thumbnail and posts the
     *  reaction to the backend.
     *
     *  @param context Caller context.
     *  @param videoFile Local stored video file.
     *  @param targetVlogId The vlog id we react to.
     *  @param isPrivate Accessibility of the video.
     */
    fun postReaction(
        context: Context,
        videoFile: File,
        targetVlogId: UUID,
        isPrivate: Boolean
    ) = compositeDisposable.add(
        reactionsUseCase.generateUploadWrapper()
            .map { uploadWrapper ->
                Completable.fromCallable {
                    // First generate thumbnail, then upload
                    val thumbnailFile = ThumbnailHelper.createThumbnailFromVideoFile(context, videoFile)

                    uploadFile(
                        context,
                        videoFile.toUri(),
                        uploadWrapper.videoUploadUri,
                        MediaConstants.VIDEO_MP4_MIME_TYPE
                    )
                    uploadFile(
                        context,
                        thumbnailFile.toUri(),
                        uploadWrapper.thumbnailUploadUri,
                        MediaConstants.IMAGE_JPEG_MIME_TYPE
                    )
                }
                    .andThen(
                        reactionsUseCase.postReaction(
                            ReactionItem.createForPosting(
                                id = uploadWrapper.id,
                                targetVlogId = targetVlogId,
                                isPrivate = isPrivate
                            ).mapToDomain()
                        )
                    )
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                        { Log.d(TAG, "Reaction posted") },
                        { Log.e(TAG, "Could not upload reaction. Message: ${it.message}") })
            }
            .subscribeOn(Schedulers.io())
            .subscribe(
                { Log.d(TAG, "Reaction wrapper created") },
                { Log.e(TAG, "Could not generate reaction upload wrapper. Message: ${it.message}") })
    )

    companion object {
        private val TAG = RecordReactionViewModel::class.java.simpleName
    }
}
