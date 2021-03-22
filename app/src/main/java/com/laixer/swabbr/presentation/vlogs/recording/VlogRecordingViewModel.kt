package com.laixer.swabbr.presentation.vlogs.recording

import android.content.Context
import android.util.Log
import androidx.core.net.toUri
import com.laixer.swabbr.domain.usecase.VlogUseCase
import com.laixer.swabbr.presentation.model.ReactionItem
import com.laixer.swabbr.presentation.model.VlogItem
import com.laixer.swabbr.presentation.model.mapToDomain
import com.laixer.swabbr.presentation.reaction.recording.RecordReactionViewModel
import com.laixer.swabbr.presentation.recording.UploadVideoViewModel
import com.laixer.swabbr.utils.files.ThumbnailHelper
import com.laixer.swabbr.utils.media.MediaConstants
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import java.io.File
import java.util.*

// TODO Duplicate functionality with [RecordReactionViewModel]. Todos located here.
// TODO Refactor, https://github.com/Laixer/Swabbr-Android/issues/153
/**
 *  View model containing functionality for recording vlogs.
 *  This includes uploading functionality.
 */
class VlogRecordingViewModel constructor(
    mHttpClient: OkHttpClient,
    private val vlogUseCase: VlogUseCase
) : UploadVideoViewModel(mHttpClient) {
    /**
     *  Uploads a [VlogItem] including thumbnail and posts it to the backend.
     *
     *  @param context Caller context. TODO Is this a resource leak? Is this the way to go?
     *  @param videoFile Local stored video file.
     *  @param isPrivate Accessibility of the video.
     */
    fun postVlog(
        context: Context,
        videoFile: File,
        isPrivate: Boolean
    ) = compositeDisposable.add(
        vlogUseCase.generateUploadWrapper()
            .map { uploadWrapper ->
                Completable.fromCallable {
                    // First generate thumbnail, then upload.
                    val thumbnailFile = ThumbnailHelper.createThumbnailFromVideoFile(context, videoFile)

                    // TODO Mime types etc declared at multiple places.
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
                        vlogUseCase.postVlog(
                            VlogItem.createForPosting(
                                id = uploadWrapper.id,
                                isPrivate = isPrivate
                            ).mapToDomain()
                        )
                    )
                    .subscribeOn(Schedulers.io())
                    .subscribe({}, {
                        Log.e(TAG, "Could not upload vlog. Message: ${it.message}")
                    })
            }
            .subscribeOn(Schedulers.io())
            .subscribe({ /* TODO Success feedback (if relevant after refactor)*/ }, {
                Log.e(TAG, "Could not generate vlog upload wrapper. Message: ${it.message}")
            })
    )

    companion object {
        private val TAG = VlogRecordingViewModel::class.java.simpleName
    }
}
